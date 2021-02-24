/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.exporter;

import net.opengis.gml._3.AbstractRingPropertyType;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LinearRingType;
import net.opengis.gml._3.PolygonType;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.rutebanken.netex.model.AuthorityRefStructure;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FareZoneRefStructure;
import org.rutebanken.netex.model.FareZoneRefs_RelStructure;
import org.rutebanken.netex.model.FareZonesInFrame_RelStructure;
import org.rutebanken.netex.model.GroupsOfStopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.OrganisationRefStructure;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.ParkingsInFrame_RelStructure;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.QuayRefStructure;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ScheduledStopPointsInFrame_RelStructure;
import org.rutebanken.netex.model.ScopingMethodEnumeration;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopAssignment_VersionStructure;
import org.rutebanken.netex.model.StopAssignmentsInFrame_RelStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlaceRefStructure;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TariffZoneRef;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.TopographicPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.ValidBetween;
import org.rutebanken.netex.model.ZoneRefStructure;
import org.rutebanken.netex.model.ZoneTopologyEnumeration;
import org.rutebanken.netex.model.Zone_VersionStructure;
import org.rutebanken.netex.validation.NeTExValidator;
import org.rutebanken.tiamat.exporter.async.NetexMappingIterator;
import org.rutebanken.tiamat.exporter.async.NetexMappingIteratorList;
import org.rutebanken.tiamat.exporter.async.NetexReferenceRemovingIterator;
import org.rutebanken.tiamat.exporter.async.ParentStopFetchingIterator;
import org.rutebanken.tiamat.exporter.async.ParentTreeTopographicPlaceFetchingIterator;
import org.rutebanken.tiamat.exporter.eviction.EntitiesEvictor;
import org.rutebanken.tiamat.exporter.eviction.SessionEntitiesEvictor;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.FareFrame;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.ServiceFrame;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.GroupOfStopPlacesRepository;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.xml.bind.JAXBContext.newInstance;

/**
 * Stream data objects inside already serialized publication delivery.
 * To be able to export many stop places wihtout keeping them all in memory.
 */
@Transactional(readOnly = true)
@Component
public class StreamingPublicationDelivery {

    private static final Logger logger = LoggerFactory.getLogger(StreamingPublicationDelivery.class);

    private static final JAXBContext publicationDeliveryContext = createContext(PublicationDeliveryStructure.class);
    private static final ObjectFactory netexObjectFactory = new ObjectFactory();

    private final StopPlaceRepository stopPlaceRepository;
    private final ParkingRepository parkingRepository;
    private final PublicationDeliveryExporter publicationDeliveryExporter;
    private final TiamatSiteFrameExporter tiamatSiteFrameExporter;
    private final TiamatServiceFrameExporter tiamatServiceFrameExporter;
    private final TiamatFareFrameExporter tiamatFareFrameExporter;
    private final NetexMapper netexMapper;
    private final TariffZoneRepository tariffZoneRepository;
    private final TopographicPlaceRepository topographicPlaceRepository;
    private final GroupOfStopPlacesRepository groupOfStopPlacesRepository;
    private final NeTExValidator neTExValidator = NeTExValidator.getNeTExValidator();
    private final NetexIdHelper netexIdHelper;
    /**
     * Validate against netex schema using the {@link NeTExValidator}
     * Enabling this for large xml files can lead to high memory consumption and/or massive performance impact.
     */
    private final boolean validateAgainstSchema;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public StreamingPublicationDelivery(StopPlaceRepository stopPlaceRepository,
                                        ParkingRepository parkingRepository,
                                        PublicationDeliveryExporter publicationDeliveryExporter,
                                        TiamatSiteFrameExporter tiamatSiteFrameExporter,
                                        TiamatServiceFrameExporter tiamatServiceFrameExporter,
                                        TiamatFareFrameExporter tiamatFareFrameExporter,
                                        NetexMapper netexMapper,
                                        TariffZoneRepository tariffZoneRepository,
                                        TopographicPlaceRepository topographicPlaceRepository,
                                        GroupOfStopPlacesRepository groupOfStopPlacesRepository,
                                        NetexIdHelper netexIdHelper,
                                        @Value("${asyncNetexExport.validateAgainstSchema:false}") boolean validateAgainstSchema) throws IOException, SAXException {
        this.stopPlaceRepository = stopPlaceRepository;
        this.parkingRepository = parkingRepository;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.tiamatSiteFrameExporter = tiamatSiteFrameExporter;
        this.tiamatServiceFrameExporter = tiamatServiceFrameExporter;
        this.tiamatFareFrameExporter = tiamatFareFrameExporter;
        this.netexMapper = netexMapper;
        this.tariffZoneRepository = tariffZoneRepository;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.groupOfStopPlacesRepository = groupOfStopPlacesRepository;
        this.netexIdHelper = netexIdHelper;
        this.validateAgainstSchema = validateAgainstSchema;
    }

    private static JAXBContext createContext(Class clazz) {
        try {
            JAXBContext jaxbContext = newInstance(clazz);
            logger.info("Created context {}", jaxbContext.getClass());
            return jaxbContext;
        } catch (JAXBException e) {
            String message = "Could not create instance of jaxb context for class " + clazz;
            logger.warn(message, e);
            throw new RuntimeException("Could not create instance of jaxb context for class " + clazz, e);
        }
    }

    public void stream(ExportParams exportParams, OutputStream outputStream) throws JAXBException, XMLStreamException, IOException, InterruptedException, SAXException {
        stream(exportParams, outputStream, false);
    }

    public void stream(ExportParams exportParams, OutputStream outputStream, boolean ignorePaging) throws JAXBException, XMLStreamException, IOException, InterruptedException, SAXException {

        org.rutebanken.tiamat.model.SiteFrame siteFrame = tiamatSiteFrameExporter.createTiamatSiteFrame("Site frame " + exportParams);
        final ServiceFrame serviceFrame = tiamatServiceFrameExporter.createTiamatServiceFrame("Service frame " + exportParams);

        final FareFrame fareFrame = tiamatFareFrameExporter.createTiamatFareFrame("Fare frame " + exportParams);

        AtomicInteger mappedStopPlaceCount = new AtomicInteger();
        AtomicInteger mappedParkingCount = new AtomicInteger();
        AtomicInteger mappedTariffZonesCount = new AtomicInteger();
        AtomicInteger mappedTopographicPlacesCount = new AtomicInteger();
        AtomicInteger mappedGroupOfStopPlacesCount = new AtomicInteger();


        EntitiesEvictor entitiesEvictor = instantiateEvictor();

        logger.info("Streaming export initiated. Export params: {}", exportParams);

        // We need to know these IDs before marshalling begins.
        // To avoid marshalling empty parking element and to be able to gather relevant topographic places
        // The primary ID represents a stop place with a certain version

        final Set<Long> stopPlacePrimaryIds = stopPlaceRepository.getDatabaseIds(exportParams, ignorePaging);
        logger.info("Got {} stop place IDs from stop place search", stopPlacePrimaryIds.size());

        //TODO: stream path links, handle export mode
        tiamatSiteFrameExporter.addRelevantPathLinks(stopPlacePrimaryIds, siteFrame);


        logger.info("Mapping site frame to netex model");
        org.rutebanken.netex.model.SiteFrame netexSiteFrame = netexMapper.mapToNetexModel(siteFrame);

        logger.info("Mapping service frame to netex model");
        final org.rutebanken.netex.model.ServiceFrame netexServiceFrame = netexMapper.mapToNetexModel(serviceFrame);

        logger.info("Mapping fare frame to netex model");
        final org.rutebanken.netex.model.FareFrame netexFareFrame = netexMapper.mapToNetexModel(fareFrame);


        logger.info("Preparing scrollable iterators");
        prepareTopographicPlaces(exportParams, stopPlacePrimaryIds, mappedTopographicPlacesCount, netexSiteFrame, entitiesEvictor);
        prepareTariffZones(exportParams, stopPlacePrimaryIds, mappedTariffZonesCount, netexSiteFrame, entitiesEvictor);
        prepareStopPlaces(exportParams, stopPlacePrimaryIds, mappedStopPlaceCount, netexSiteFrame, entitiesEvictor);
        prepareParkings(exportParams, stopPlacePrimaryIds, mappedParkingCount, netexSiteFrame, entitiesEvictor);
        prepareGroupOfStopPlaces(exportParams, stopPlacePrimaryIds, mappedGroupOfStopPlacesCount, netexSiteFrame, entitiesEvictor);


        PublicationDeliveryStructure publicationDeliveryStructure;

        if (exportParams.getServiceFrameExportMode() == ExportParams.ExportMode.ALL) {
            prepareScheduledStopPoints(stopPlacePrimaryIds, netexServiceFrame);
            prepareFareZones(netexFareFrame);
            publicationDeliveryStructure = publicationDeliveryExporter.createPublicationDelivery(netexSiteFrame, netexServiceFrame,netexFareFrame);
        } else {
            publicationDeliveryStructure = publicationDeliveryExporter.createPublicationDelivery(netexSiteFrame);
        }

        Marshaller marshaller = createMarshaller();

        logger.info("Start marshalling publication delivery");
        marshaller.marshal(netexObjectFactory.createPublicationDelivery(publicationDeliveryStructure), outputStream);
        logger.info("Mapped {} stop places, {} parkings, {} topographic places, {} group of stop places and {} tariff zones to netex",
                mappedStopPlaceCount.get(),
                mappedParkingCount.get(),
                mappedTopographicPlacesCount,
                mappedGroupOfStopPlacesCount,
                mappedTariffZonesCount);

    }

    private void prepareFareZones(org.rutebanken.netex.model.FareFrame netexFareFrame) {

        //TODO: Just a place    holder

        /*
        <FareZone id="TST:FareZone:1" version="1">
          <Name lang="nob">Farezone Test</Name>
          <ZoneTopology>sequence</ZoneTopology>
          <ScopingMethod>explicitStops</ScopingMethod>
          <AuthorityRef ref="TST:Authority:TST"/>
          <neighbours>
            <FareZoneRef ref="TST:FareZone:2"/>
          </neighbours>
        </FareZone>
         */

        List<FareZone> fareZones = new ArrayList<>();
        final FareZone fareZone1 = new FareZone().withVersion("1");
        fareZone1.withId("RUT:FareZone:1");
        fareZone1.withName(new MultilingualString().withValue("Ruter# FareZone 1").withLang("no"));
        fareZone1.setZoneTopology(ZoneTopologyEnumeration.TILED);
        fareZone1.setScopingMethod(ScopingMethodEnumeration.IMPLICIT_SPATIAL_PROJECTION);

        net.opengis.gml._3.ObjectFactory openGisObjectFactory = new net.opengis.gml._3.ObjectFactory();

        List<Double> values = new ArrayList<>();
        values.add(9.8468);
        values.add(59.2649);
        values.add(9.8456);
        values.add(59.2654);
        values.add(9.8457);
        values.add(59.2655);
        values.add(9.8443);
        values.add(59.2663);
        values.add(values.get(0));
        values.add(values.get(1));

        DirectPositionListType positionList = new DirectPositionListType().withValue(values);

        LinearRingType linearRing = new LinearRingType()
                .withPosList(positionList);

        PolygonType polygonType = new PolygonType()
                .withId("RUT-01")
                .withExterior(new AbstractRingPropertyType()
                        .withAbstractRing(openGisObjectFactory.createLinearRing(linearRing)));

        fareZone1.withPolygon(polygonType);

        fareZone1.withParentZoneRef(new ZoneRefStructure().withRef("RUT:TariffZone:2Ø").withValue("1"));


        final JAXBElement<AuthorityRefStructure> authorityRef = new ObjectFactory().createAuthorityRef(new AuthorityRefStructure().withRef("ENT:Authority:RUT"));
        fareZone1.withTransportOrganisationRef(authorityRef);

        FareZoneRefs_RelStructure fareZoneNeighbours = new FareZoneRefs_RelStructure().withFareZoneRef(new FareZoneRefStructure().withRef("RUT:FareZone:2V").withVersion("1"));
        fareZone1.withNeighbours(fareZoneNeighbours);

        fareZones.add(fareZone1);

        final FareZone fareZone2V = new FareZone().withVersion("1");
        fareZone2V.withId("RUT:FareZone:2V");
        fareZone2V.withName(new MultilingualString().withValue("Ruter# FareZone 2V").withLang("no"));
        fareZone2V.setZoneTopology(ZoneTopologyEnumeration.TILED);
        fareZone2V.setScopingMethod(ScopingMethodEnumeration.IMPLICIT_SPATIAL_PROJECTION);


        fareZone1.withTransportOrganisationRef(authorityRef);

        FareZoneRefs_RelStructure fareZoneNeighbours2V = new FareZoneRefs_RelStructure().withFareZoneRef(new FareZoneRefStructure().withRef("RUT:FareZone:1").withVersion("1"));
        fareZone2V.withNeighbours(fareZoneNeighbours2V);



        fareZones.add(fareZone2V);


        FareZonesInFrame_RelStructure fareZonesInFrameRelStructure = new FareZonesInFrame_RelStructure();
        setField(FareZonesInFrame_RelStructure.class,"fareZone", fareZonesInFrameRelStructure, fareZones);
        netexFareFrame.setFareZones(fareZonesInFrameRelStructure);
    }

    private void prepareTariffZones(ExportParams exportParams, Set<Long> stopPlacePrimaryIds, AtomicInteger mappedTariffZonesCount, SiteFrame netexSiteFrame, EntitiesEvictor evicter) {


        Iterator<org.rutebanken.tiamat.model.TariffZone> tariffZoneIterator;
        if (exportParams.getTariffZoneExportMode() == null || exportParams.getTariffZoneExportMode().equals(ExportParams.ExportMode.ALL)) {

            logger.info("Preparing to scroll all tariff zones, regardless of version");
            tariffZoneIterator = tariffZoneRepository.scrollTariffZones();
        } else if (exportParams.getTariffZoneExportMode().equals(ExportParams.ExportMode.RELEVANT)) {

            logger.info("Preparing to scroll relevant tariff zones from stop place ids");
            tariffZoneIterator = tariffZoneRepository.scrollTariffZones(stopPlacePrimaryIds);
        } else {
            logger.info("Tariff zone export mode is {}. Will not export tariff zones", exportParams.getTariffZoneExportMode());
            tariffZoneIterator = Collections.emptyIterator();
        }

        List<JAXBElement<? extends Zone_VersionStructure>> netexTariffZones = new ArrayList<>();
        while (tariffZoneIterator.hasNext()) {
            final TariffZone tariffZone = netexMapper.mapToNetexModel(tariffZoneIterator.next());
            final JAXBElement<TariffZone> tariffZoneJAXBElement = new ObjectFactory().createTariffZone(tariffZone);
            netexTariffZones.add(tariffZoneJAXBElement);
            mappedTariffZonesCount.incrementAndGet();

        }
        if (!netexTariffZones.isEmpty()) {
            var tariffZonesInFrameRelStructure = new TariffZonesInFrame_RelStructure();
            setField(TariffZonesInFrame_RelStructure.class, "tariffZone", tariffZonesInFrameRelStructure, netexTariffZones);
            netexSiteFrame.setTariffZones(tariffZonesInFrameRelStructure);
        } else {
            logger.info("No tariff zones to export");
            netexSiteFrame.setTariffZones(null);
        }

    }

    private void prepareParkings(ExportParams exportParams, Set<Long> stopPlacePrimaryIds, AtomicInteger mappedParkingCount, SiteFrame netexSiteFrame, EntitiesEvictor evicter) {

        // ExportParams could be used for parkingExportMode.

        int parkingsCount = parkingRepository.countResult(stopPlacePrimaryIds);
        if (parkingsCount > 0) {
            // Only set parkings if they will exist during marshalling.
            logger.info("Parking count is {}, will create parking in publication delivery", parkingsCount);
            ParkingsInFrame_RelStructure parkingsInFrame_relStructure = new ParkingsInFrame_RelStructure();
            List<Parking> parkings = new NetexMappingIteratorList<>(() -> new NetexMappingIterator<>(netexMapper, parkingRepository.scrollParkings(stopPlacePrimaryIds),
                    Parking.class, mappedParkingCount, evicter));

            setField(ParkingsInFrame_RelStructure.class, "parking", parkingsInFrame_relStructure, parkings);
            netexSiteFrame.setParkings(parkingsInFrame_relStructure);
        } else {
            logger.info("No parkings to export based on stop places");
        }
    }

    private void prepareStopPlaces(ExportParams exportParams, Set<Long> stopPlacePrimaryIds, AtomicInteger mappedStopPlaceCount, SiteFrame netexSiteFrame, EntitiesEvictor evicter) {
        // Override lists with custom iterator to be able to scroll database results on the fly.
        if (!stopPlacePrimaryIds.isEmpty()) {
            logger.info("There are stop places to export");

            final Iterator<org.rutebanken.tiamat.model.StopPlace> stopPlaceIterator = stopPlaceRepository.scrollStopPlaces(stopPlacePrimaryIds);
            StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

            // Use Listening iterator to collect stop place IDs.
            ParentStopFetchingIterator parentStopFetchingIterator = new ParentStopFetchingIterator(stopPlaceIterator, stopPlaceRepository);
            NetexMappingIterator<org.rutebanken.tiamat.model.StopPlace, StopPlace> netexMappingIterator = new NetexMappingIterator<>(netexMapper, parentStopFetchingIterator, StopPlace.class, mappedStopPlaceCount, evicter);

            List<StopPlace> stopPlaces = new NetexMappingIteratorList<>(() -> new NetexReferenceRemovingIterator(netexMappingIterator, exportParams));
            setField(StopPlacesInFrame_RelStructure.class, "stopPlace", stopPlacesInFrame_relStructure, stopPlaces);
            netexSiteFrame.setStopPlaces(stopPlacesInFrame_relStructure);
        } else {
            logger.info("No stop places to export");
        }
    }

    private void prepareScheduledStopPoints(Set<Long> stopPlacePrimaryIds, org.rutebanken.netex.model.ServiceFrame netexServiceFrame) {
        if (!stopPlacePrimaryIds.isEmpty()) {
            logger.info("There are stop places to export");

            final Iterator<org.rutebanken.tiamat.model.StopPlace> stopPlaceIterator = stopPlaceRepository.scrollStopPlaces(stopPlacePrimaryIds);

            // Use Listening iterator to collect stop place IDs.
            ParentStopFetchingIterator parentStopFetchingIterator = new ParentStopFetchingIterator(stopPlaceIterator, stopPlaceRepository);

            List<ScheduledStopPoint> netexScheduledStopPoints = new ArrayList<>();

            List<JAXBElement<? extends StopAssignment_VersionStructure>> stopAssignment = new ArrayList<>();


            while (parentStopFetchingIterator.hasNext()) {
                final org.rutebanken.tiamat.model.StopPlace stopPlace = parentStopFetchingIterator.next();
                covertStopPlaceToScheduledStopPoint(netexScheduledStopPoints, stopAssignment, stopPlace);

            }


            if (!netexScheduledStopPoints.isEmpty()) {
                final ScheduledStopPointsInFrame_RelStructure scheduledStopPointsInFrame_relStructure = new ScheduledStopPointsInFrame_RelStructure();
                setField(ScheduledStopPointsInFrame_RelStructure.class, "scheduledStopPoint", scheduledStopPointsInFrame_relStructure, netexScheduledStopPoints);
                netexServiceFrame.setScheduledStopPoints(scheduledStopPointsInFrame_relStructure);


                StopAssignmentsInFrame_RelStructure stopAssignmentsInFrame_RelStructure = new StopAssignmentsInFrame_RelStructure();
                setField(StopAssignmentsInFrame_RelStructure.class, "stopAssignment", stopAssignmentsInFrame_RelStructure, stopAssignment);
                netexServiceFrame.setStopAssignments(stopAssignmentsInFrame_RelStructure);
            }

        }
    }

    private void covertStopPlaceToScheduledStopPoint(List<ScheduledStopPoint> scheduledStopPoints, List<JAXBElement<? extends StopAssignment_VersionStructure>> netexPassengerStopAssignment, org.rutebanken.tiamat.model.StopPlace stopPlace) {

        // Add stop place

        final String netexId = stopPlace.getNetexId();
        String stopPlaceName = null;
        if (stopPlace.getName() != null) {
            stopPlaceName = stopPlace.getName().getValue();
        }
        final long version = stopPlace.getVersion();
        var stopPlaceNetexId = netexIdHelper.extractIdPostfix(netexId);
        var idPrefix = netexIdHelper.extractIdPrefix(netexId);
        var scheduledStopPointNetexId = idPrefix + ":ScheduledStopPoint:S" + stopPlaceNetexId;

        LocalDateTime validFrom = null;
        LocalDateTime validTo = null;
        if (stopPlace.getValidBetween() != null) {
            if (stopPlace.getValidBetween().getFromDate() != null) {
                validFrom = LocalDateTime.ofInstant(stopPlace.getValidBetween().getFromDate(), ZoneId.systemDefault());
            }
            if (stopPlace.getValidBetween().getToDate() != null) {
                validTo = LocalDateTime.ofInstant(stopPlace.getValidBetween().getToDate(), ZoneId.systemDefault());
            }
        }


        scheduledStopPoints.add(createNetexScheduledStopPoint(scheduledStopPointNetexId, stopPlaceName, version, validFrom, validTo));

        netexPassengerStopAssignment.add(createPassengerStopAssignment(netexId, version, scheduledStopPointNetexId, netexPassengerStopAssignment.size() + 1, validFrom, validTo, false));

        // Add quays
        final Set<Quay> quays = stopPlace.getQuays();
        for (Quay quay : quays) {
            var quayNetexId = netexIdHelper.extractIdPostfix(quay.getNetexId());
            var quayUdPrefix = netexIdHelper.extractIdPrefix(quay.getNetexId());
            var quayScheduledStopPointNetexId = quayUdPrefix + ":ScheduledStopPoint:Q" + quayNetexId;
            scheduledStopPoints.add(createNetexScheduledStopPoint(quayScheduledStopPointNetexId, stopPlaceName, quay.getVersion(), validFrom, validTo));
            netexPassengerStopAssignment.add(createPassengerStopAssignment(quay.getNetexId(), quay.getVersion(), quayScheduledStopPointNetexId, netexPassengerStopAssignment.size() + 1, validFrom, validTo, true));

        }

    }

    private JAXBElement<? extends StopAssignment_VersionStructure> createPassengerStopAssignment(String netexId, long version, String scheduledStopPointNetexId, int passengerStopAssignmentOrder, LocalDateTime validFrom, LocalDateTime validTo, boolean isQuay) {

        var passengerStopAssignmentId = netexIdHelper.extractIdPostfix(scheduledStopPointNetexId);
        var idPrefix= netexIdHelper.extractIdPrefix(scheduledStopPointNetexId);
        final PassengerStopAssignment passengerStopAssignment = new PassengerStopAssignment();
        passengerStopAssignment.withId(idPrefix + ":PassengerStopAssignment:P" + passengerStopAssignmentId);
        passengerStopAssignment.withVersion(String.valueOf(version));
        passengerStopAssignment.withOrder(BigInteger.valueOf(passengerStopAssignmentOrder));

        ValidBetween validBetween = new ValidBetween().withFromDate(validFrom).withToDate(validTo);
        passengerStopAssignment.withValidBetween(validBetween);
        if (isQuay) {
            passengerStopAssignment.withQuayRef(new QuayRefStructure().withRef(netexId).withVersion(String.valueOf(version)));
        } else {
            passengerStopAssignment.withStopPlaceRef(new StopPlaceRefStructure().withRef(netexId).withVersion(String.valueOf(version)));
        }
        final JAXBElement<ScheduledStopPointRefStructure> scheduledStopPointRef = new ObjectFactory().createScheduledStopPointRef(new ScheduledStopPointRefStructure().withRef(scheduledStopPointNetexId).withVersionRef(String.valueOf(version)));
        passengerStopAssignment.withScheduledStopPointRef(scheduledStopPointRef);

        return new ObjectFactory().createPassengerStopAssignment(passengerStopAssignment);

    }

    private ScheduledStopPoint createNetexScheduledStopPoint(String scheduledStopPointNetexId, String stopPlaceName, long version, LocalDateTime validFrom, LocalDateTime validTo) {
        final org.rutebanken.netex.model.ScheduledStopPoint netexScheduledStopPoint = new org.rutebanken.netex.model.ScheduledStopPoint();
        netexScheduledStopPoint.setId(scheduledStopPointNetexId);
        netexScheduledStopPoint.setVersion(String.valueOf(version));
        netexScheduledStopPoint.withName(new MultilingualString().withValue(stopPlaceName));
        ValidBetween validBetween = new ValidBetween().withFromDate(validFrom).withToDate(validTo);

        netexScheduledStopPoint.withValidBetween(validBetween);

        return netexScheduledStopPoint;
    }

    private void prepareTopographicPlaces(ExportParams exportParams, Set<Long> stopPlacePrimaryIds, AtomicInteger mappedTopographicPlacesCount, SiteFrame netexSiteFrame, EntitiesEvictor evicter) {

        Iterator<TopographicPlace> relevantTopographicPlacesIterator;

        if (exportParams.getTopographicPlaceExportMode() == null || exportParams.getTopographicPlaceExportMode().equals(ExportParams.ExportMode.ALL)) {
            logger.info("Prepare scrolling for all topographic places");
            relevantTopographicPlacesIterator = topographicPlaceRepository.scrollTopographicPlaces();

        } else if (exportParams.getTopographicPlaceExportMode().equals(ExportParams.ExportMode.RELEVANT)) {
            logger.info("Prepare scrolling relevant topographic places");
            relevantTopographicPlacesIterator = new ParentTreeTopographicPlaceFetchingIterator(topographicPlaceRepository.scrollTopographicPlaces(stopPlacePrimaryIds), topographicPlaceRepository);
        } else {
            logger.info("Topographic export mode is {}. Will not export topographic places", exportParams.getTopographicPlaceExportMode());
            relevantTopographicPlacesIterator = Collections.emptyIterator();
        }

        if (relevantTopographicPlacesIterator.hasNext()) {

            NetexMappingIterator<TopographicPlace, org.rutebanken.netex.model.TopographicPlace> topographicPlaceNetexMappingIterator = new NetexMappingIterator<>(
                    netexMapper, relevantTopographicPlacesIterator, org.rutebanken.netex.model.TopographicPlace.class, mappedTopographicPlacesCount, evicter);

            List<org.rutebanken.netex.model.TopographicPlace> topographicPlaces = new NetexMappingIteratorList<>(() -> topographicPlaceNetexMappingIterator);

            TopographicPlacesInFrame_RelStructure topographicPlacesInFrame_relStructure = new TopographicPlacesInFrame_RelStructure();
            setField(TopographicPlacesInFrame_RelStructure.class, "topographicPlace", topographicPlacesInFrame_relStructure, topographicPlaces);
            netexSiteFrame.setTopographicPlaces(topographicPlacesInFrame_relStructure);
        } else {
            netexSiteFrame.setTopographicPlaces(null);
        }
    }

    private void prepareGroupOfStopPlaces(ExportParams exportParams, Set<Long> stopPlacePrimaryIds, AtomicInteger mappedGroupOfStopPlacesCount, SiteFrame netexSiteFrame, EntitiesEvictor evicter) {

        Iterator<GroupOfStopPlaces> groupOfStopPlacesIterator;

        if (exportParams.getGroupOfStopPlacesExportMode() == null || exportParams.getGroupOfStopPlacesExportMode().equals(ExportParams.ExportMode.ALL)) {
            logger.info("Prepare scrolling for all group of stop places");
            groupOfStopPlacesIterator = groupOfStopPlacesRepository.scrollGroupOfStopPlaces();

        } else if (exportParams.getGroupOfStopPlacesExportMode().equals(ExportParams.ExportMode.RELEVANT)) {
            logger.info("Prepare scrolling relevant group of stop places");
            groupOfStopPlacesIterator = groupOfStopPlacesRepository.scrollGroupOfStopPlaces(stopPlacePrimaryIds);
        } else {
            logger.info("Group of stop places export mode is {}. Will not export group of stop places", exportParams.getGroupOfStopPlacesExportMode());
            groupOfStopPlacesIterator = Collections.emptyIterator();
        }

        if (groupOfStopPlacesIterator.hasNext()) {

            NetexMappingIterator<GroupOfStopPlaces, org.rutebanken.netex.model.GroupOfStopPlaces> netexMappingIterator = new NetexMappingIterator<>(
                    netexMapper, groupOfStopPlacesIterator, org.rutebanken.netex.model.GroupOfStopPlaces.class, mappedGroupOfStopPlacesCount, evicter);

            List<org.rutebanken.netex.model.GroupOfStopPlaces> groupOfStopPlacesList = new NetexMappingIteratorList<>(() -> netexMappingIterator);

            GroupsOfStopPlacesInFrame_RelStructure groupsOfStopPlacesInFrame_relStructure = new GroupsOfStopPlacesInFrame_RelStructure();
            setField(GroupsOfStopPlacesInFrame_RelStructure.class, "groupOfStopPlaces", groupsOfStopPlacesInFrame_relStructure, groupOfStopPlacesList);
            netexSiteFrame.setGroupsOfStopPlaces(groupsOfStopPlacesInFrame_relStructure);
        } else {
            netexSiteFrame.setGroupsOfStopPlaces(null);
        }
    }

    private EntitiesEvictor instantiateEvictor() {
        if (entityManager != null) {
            Session currentSession = entityManager.unwrap(Session.class);
            return new SessionEntitiesEvictor((SessionImpl) currentSession);
        } else {
            return new EntitiesEvictor() {
                @Override
                public void evictKnownEntitiesFromSession(Object entity) {
                    // Intentionally left blank
                }
            };
        }
    }

    /**
     * Set field value with reflection.
     * Used for setting list values in netex model.
     */
    private void setField(Class clazz, String fieldName, Object instance, Object fieldValue) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, fieldValue);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Cannot set field " + fieldName + " of " + instance, e);
        }
    }

    private Marshaller createMarshaller() throws JAXBException, IOException, SAXException {
        Marshaller marshaller = publicationDeliveryContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");

        if (validateAgainstSchema) {
            marshaller.setSchema(neTExValidator.getSchema());
        }

        return marshaller;
    }
}
