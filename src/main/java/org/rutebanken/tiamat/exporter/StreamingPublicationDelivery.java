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

import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.rutebanken.netex.model.GroupsOfStopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.ParkingsInFrame_RelStructure;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.QuayRefStructure;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ScheduledStopPointsInFrame_RelStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopAssignment_VersionStructure;
import org.rutebanken.netex.model.StopAssignmentsInFrame_RelStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlaceRefStructure;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.TopographicPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.ValidBetween;
import org.rutebanken.netex.validation.NeTExValidator;
import org.rutebanken.tiamat.exporter.async.NetexMappingIterator;
import org.rutebanken.tiamat.exporter.async.NetexMappingIteratorList;
import org.rutebanken.tiamat.exporter.async.NetexReferenceRemovingIterator;
import org.rutebanken.tiamat.exporter.async.ParentStopFetchingIterator;
import org.rutebanken.tiamat.exporter.async.ParentTreeTopographicPlaceFetchingIterator;
import org.rutebanken.tiamat.exporter.eviction.EntitiesEvictor;
import org.rutebanken.tiamat.exporter.eviction.SessionEntitiesEvictor;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.ServiceFrame;
import org.rutebanken.tiamat.model.TopographicPlace;
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
    private final NetexMapper netexMapper;
    private final TariffZoneRepository tariffZoneRepository;
    private final TopographicPlaceRepository topographicPlaceRepository;
    private final GroupOfStopPlacesRepository groupOfStopPlacesRepository;
    private final NeTExValidator neTExValidator = NeTExValidator.getNeTExValidator();
    /**
     * Validate against netex schema using the {@link NeTExValidator}
     * Enabling this for large xml files can lead to high memory consumption and/or massive performance impact.
     */
    private final boolean validateAgainstSchema;
    private int passengerStopAssignmentOrder;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public StreamingPublicationDelivery(StopPlaceRepository stopPlaceRepository,
                                        ParkingRepository parkingRepository,
                                        PublicationDeliveryExporter publicationDeliveryExporter,
                                        TiamatSiteFrameExporter tiamatSiteFrameExporter,
                                        TiamatServiceFrameExporter tiamatServiceFrameExporter,
                                        NetexMapper netexMapper,
                                        TariffZoneRepository tariffZoneRepository,
                                        TopographicPlaceRepository topographicPlaceRepository,
                                        GroupOfStopPlacesRepository groupOfStopPlacesRepository,
                                        @Value("${asyncNetexExport.validateAgainstSchema:false}") boolean validateAgainstSchema) throws IOException, SAXException {
        this.stopPlaceRepository = stopPlaceRepository;
        this.parkingRepository = parkingRepository;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.tiamatSiteFrameExporter = tiamatSiteFrameExporter;
        this.tiamatServiceFrameExporter = tiamatServiceFrameExporter;
        this.netexMapper = netexMapper;
        this.tariffZoneRepository = tariffZoneRepository;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.groupOfStopPlacesRepository = groupOfStopPlacesRepository;
        this.validateAgainstSchema = validateAgainstSchema;
        this.passengerStopAssignmentOrder = 1;
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


        logger.info("Preparing scrollable iterators");
        prepareTopographicPlaces(exportParams, stopPlacePrimaryIds, mappedTopographicPlacesCount, netexSiteFrame, entitiesEvictor);
        prepareTariffZones(exportParams, stopPlacePrimaryIds, mappedTariffZonesCount, netexSiteFrame, entitiesEvictor);
        prepareStopPlaces(exportParams, stopPlacePrimaryIds, mappedStopPlaceCount, netexSiteFrame, entitiesEvictor);
        prepareParkings(exportParams, stopPlacePrimaryIds, mappedParkingCount, netexSiteFrame, entitiesEvictor);
        prepareGroupOfStopPlaces(exportParams, stopPlacePrimaryIds, mappedGroupOfStopPlacesCount, netexSiteFrame, entitiesEvictor);

        prepareScheduledStopPoints(exportParams, stopPlacePrimaryIds, netexServiceFrame);

        PublicationDeliveryStructure publicationDeliveryStructure;
        if (exportParams.getServiceFrameExportMode() == ExportParams.ExportMode.ALL) {
            publicationDeliveryStructure = publicationDeliveryExporter.createPublicationDelivery(netexSiteFrame, netexServiceFrame);
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
        // Rest passenger order
        passengerStopAssignmentOrder = 1;
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

        if (tariffZoneIterator.hasNext()) {
            NetexMappingIterator<org.rutebanken.tiamat.model.TariffZone, TariffZone> tariffZoneMappingIterator =
                    new NetexMappingIterator<>(netexMapper, tariffZoneIterator, TariffZone.class, mappedTariffZonesCount, evicter);

            List<TariffZone> tariffZones = new NetexMappingIteratorList<>(() -> tariffZoneMappingIterator);

            TariffZonesInFrame_RelStructure tariffZonesInFrame_relStructure = new TariffZonesInFrame_RelStructure();
            setField(TariffZonesInFrame_RelStructure.class, "tariffZone", tariffZonesInFrame_relStructure, tariffZones);
            netexSiteFrame.setTariffZones(tariffZonesInFrame_relStructure);
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

    private void prepareScheduledStopPoints(ExportParams exportParams, Set<Long> stopPlacePrimaryIds, org.rutebanken.netex.model.ServiceFrame netexServiceFrame) {
        if (exportParams.getServiceFrameExportMode() == ExportParams.ExportMode.ALL && !stopPlacePrimaryIds.isEmpty()) {
            logger.info("There are stop places to export");

            final Iterator<org.rutebanken.tiamat.model.StopPlace> stopPlaceIterator = stopPlaceRepository.scrollStopPlaces(stopPlacePrimaryIds);

            List<ScheduledStopPoint> netexScheduledStopPoints = new ArrayList<>();

            List<JAXBElement<? extends StopAssignment_VersionStructure>> stopAssignment = new ArrayList<>();


            while (stopPlaceIterator.hasNext()) {
                final org.rutebanken.tiamat.model.StopPlace stopPlace = stopPlaceIterator.next();
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
        var stopPlaceNetexId = netexId.split(":")[2];
        var scheduledStopPointNetexId = "NSR:ScheduledStopPoint:S" + stopPlaceNetexId;

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
        netexPassengerStopAssignment.add(createPassengerStopAssignment(netexId, version, scheduledStopPointNetexId, passengerStopAssignmentOrder, validFrom, validTo, false));
        passengerStopAssignmentOrder++;
        // Add quays
        final Set<Quay> quays = stopPlace.getQuays();
        for (Quay quay : quays) {
            final String netexId1 = quay.getNetexId().split(":")[2];
            var scheduledStopPointNetexId2 = "NSR:ScheduledStopPoint:Q" + netexId1;
            scheduledStopPoints.add(createNetexScheduledStopPoint(scheduledStopPointNetexId2, stopPlaceName, version, validFrom, validTo));
            netexPassengerStopAssignment.add(createPassengerStopAssignment(quay.getNetexId(), quay.getVersion(), scheduledStopPointNetexId2, passengerStopAssignmentOrder, validFrom, validTo, true));

            passengerStopAssignmentOrder++;
        }

    }

    private JAXBElement<? extends StopAssignment_VersionStructure> createPassengerStopAssignment(String netexId, long version, String scheduledStopPointNetexId, int passengerStopAssignmentOrder, LocalDateTime validFrom, LocalDateTime validTo, boolean isQuay) {

        final String passengerStopAssignmentId = scheduledStopPointNetexId.split(":")[2];

        final PassengerStopAssignment passengerStopAssignment = new PassengerStopAssignment();
        passengerStopAssignment.withId("NSR:PassengerStopAssignment:P" + passengerStopAssignmentId);
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
