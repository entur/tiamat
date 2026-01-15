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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.hibernate.internal.SessionImpl;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FareZonesInFrame_RelStructure;
import org.rutebanken.netex.model.GroupsOfStopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.GroupsOfTariffZonesInFrame_RelStructure;
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
import org.rutebanken.tiamat.model.GroupOfTariffZones;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.ResourceFrame;
import org.rutebanken.tiamat.model.ServiceFrame;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.rutebanken.tiamat.repository.GroupOfStopPlacesRepository;
import org.rutebanken.tiamat.repository.GroupOfTariffZonesRepository;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.PurposeOfGroupingRepository;
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

import static jakarta.xml.bind.JAXBContext.newInstance;

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
    private final PublicationDeliveryCreator publicationDeliveryCreator;
    private final TiamatSiteFrameExporter tiamatSiteFrameExporter;
    private final TiamatServiceFrameExporter tiamatServiceFrameExporter;
    private final TiamatFareFrameExporter tiamatFareFrameExporter;

    private final TiamatResourceFrameExporter tiamatResourceFrameExporter;
    private final NetexMapper netexMapper;
    private final TariffZoneRepository tariffZoneRepository;
    private final FareZoneRepository fareZoneRepository;
    private final TopographicPlaceRepository topographicPlaceRepository;
    private final GroupOfStopPlacesRepository groupOfStopPlacesRepository;
    private final GroupOfTariffZonesRepository groupOfTariffZonesRepository;
    private final NeTExValidator neTExValidator = NeTExValidator.getNeTExValidator();
    /**
     * Validate against netex schema using the {@link NeTExValidator}
     * Enabling this for large xml files can lead to high memory consumption and/or massive performance impact.
     */
    private final boolean validateAgainstSchema;
    private final ServiceFrameElementCreator serviceFrameElementCreator;

    @PersistenceContext
    private EntityManager entityManager;
    private final PurposeOfGroupingRepository purposeOfGroupingRepository;

    @Autowired
    public StreamingPublicationDelivery(StopPlaceRepository stopPlaceRepository,
                                        ParkingRepository parkingRepository,
                                        PublicationDeliveryCreator publicationDeliveryCreator,
                                        TiamatSiteFrameExporter tiamatSiteFrameExporter,
                                        TiamatServiceFrameExporter tiamatServiceFrameExporter,
                                        TiamatFareFrameExporter tiamatFareFrameExporter,
                                        TiamatResourceFrameExporter tiamatResourceFrameExporter,
                                        NetexMapper netexMapper,
                                        TariffZoneRepository tariffZoneRepository,
                                        FareZoneRepository fareZoneRepository,
                                        TopographicPlaceRepository topographicPlaceRepository,
                                        GroupOfStopPlacesRepository groupOfStopPlacesRepository,
                                        GroupOfTariffZonesRepository groupOfTariffZonesRepository,
                                        @Value("${asyncNetexExport.validateAgainstSchema:false}") boolean validateAgainstSchema,
                                        PurposeOfGroupingRepository purposeOfGroupingRepository,
                                        ServiceFrameElementCreator serviceFrameElementCreator) throws IOException, SAXException {
        this.stopPlaceRepository = stopPlaceRepository;
        this.parkingRepository = parkingRepository;
        this.publicationDeliveryCreator = publicationDeliveryCreator;
        this.tiamatSiteFrameExporter = tiamatSiteFrameExporter;
        this.tiamatServiceFrameExporter = tiamatServiceFrameExporter;
        this.tiamatFareFrameExporter = tiamatFareFrameExporter;
        this.tiamatResourceFrameExporter = tiamatResourceFrameExporter;
        this.netexMapper = netexMapper;
        this.tariffZoneRepository = tariffZoneRepository;
        this.fareZoneRepository = fareZoneRepository;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.groupOfStopPlacesRepository = groupOfStopPlacesRepository;
        this.groupOfTariffZonesRepository = groupOfTariffZonesRepository;
        this.validateAgainstSchema = validateAgainstSchema;
        this.purposeOfGroupingRepository = purposeOfGroupingRepository;
        this.serviceFrameElementCreator = serviceFrameElementCreator;
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

        final ResourceFrame resourceFrame = tiamatResourceFrameExporter.createTiamatResourceFrame("Resource frame"+ exportParams);


        AtomicInteger mappedStopPlaceCount = new AtomicInteger();
        AtomicInteger mappedParkingCount = new AtomicInteger();
        AtomicInteger mappedTariffZonesCount = new AtomicInteger();
        AtomicInteger mappedFareZonesCount = new AtomicInteger();
        AtomicInteger mappedTopographicPlacesCount = new AtomicInteger();
        AtomicInteger mappedGroupOfStopPlacesCount = new AtomicInteger();
        AtomicInteger mappedGroupOfTariffZonesCount = new AtomicInteger();


        EntitiesEvictor entitiesEvictor = instantiateEvictor();

        logger.info("Streaming export initiated. Export params: {}", exportParams);

        // We need to know these IDs before marshalling begins.
        // To avoid marshalling empty parking element and to be able to gather relevant topographic places
        // The primary ID represents a stop place with a certain version

        final Set<Long> stopPlacePrimaryIds = stopPlaceRepository.getDatabaseIds(exportParams, ignorePaging);
        logger.info("Got {} stop place IDs from stop place search", stopPlacePrimaryIds.size());

        tiamatSiteFrameExporter.addRelevantPathLinks(stopPlacePrimaryIds, siteFrame);


        logger.info("Mapping site frame to netex model");
        org.rutebanken.netex.model.SiteFrame netexSiteFrame = netexMapper.mapToNetexModel(siteFrame);
        logger.info("Mapping service frame to netex model");
        final org.rutebanken.netex.model.ServiceFrame netexServiceFrame = netexMapper.mapToNetexModel(serviceFrame);

        logger.info("Mapping fare frame to netex model");
        final org.rutebanken.netex.model.FareFrame netexFareFrame = netexMapper.mapToNetexModel(fareFrame);

        logger.info("Mapping resource frame to netex model");
        final org.rutebanken.netex.model.ResourceFrame netexResourceFrame = netexMapper.mapToNetexModel(resourceFrame);


        logger.info("Preparing scrollable iterators");
        prepareStopPlaces(exportParams, stopPlacePrimaryIds, mappedStopPlaceCount, netexSiteFrame, entitiesEvictor);
        prepareTopographicPlaces(exportParams, stopPlacePrimaryIds, mappedTopographicPlacesCount, netexSiteFrame, entitiesEvictor);
        prepareTariffZones(exportParams, stopPlacePrimaryIds, mappedTariffZonesCount, netexSiteFrame, entitiesEvictor);
        prepareParkings(exportParams, stopPlacePrimaryIds, mappedParkingCount, netexSiteFrame, entitiesEvictor);
        prepareGroupOfStopPlaces(exportParams, stopPlacePrimaryIds, mappedGroupOfStopPlacesCount, netexSiteFrame,netexResourceFrame, entitiesEvictor);
        prepareFareZones(exportParams,stopPlacePrimaryIds,mappedFareZonesCount,mappedGroupOfTariffZonesCount,netexSiteFrame,netexFareFrame,entitiesEvictor);
        prepareScheduledStopPoints(stopPlacePrimaryIds, netexServiceFrame);


        PublicationDeliveryStructure publicationDeliveryStructure;
        if(!exportParams.getFareZoneExportMode().equals(ExportParams.ExportMode.NONE)) {
            if(exportParams.getGroupOfStopPlacesExportMode().equals(ExportParams.ExportMode.NONE)) {
                publicationDeliveryStructure = publicationDeliveryCreator.createPublicationDelivery(netexSiteFrame, netexServiceFrame, netexFareFrame);
            } else {
                publicationDeliveryStructure = publicationDeliveryCreator.createPublicationDelivery(netexSiteFrame, netexServiceFrame, netexFareFrame, netexResourceFrame);
            }
        } else {
            if(exportParams.getGroupOfStopPlacesExportMode().equals(ExportParams.ExportMode.NONE)) {
                publicationDeliveryStructure = publicationDeliveryCreator.createPublicationDelivery(netexSiteFrame, netexServiceFrame);
            } else {
                publicationDeliveryStructure = publicationDeliveryCreator.createPublicationDelivery(netexSiteFrame, netexServiceFrame, netexResourceFrame);
            }
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

    private void prepareFareZones(ExportParams exportParams,Set<Long> stopPlacePrimaryIds, AtomicInteger mappedFareZonesCount, AtomicInteger mappedGroupOfTariffZonesCount, SiteFrame netexSiteFrame, org.rutebanken.netex.model.FareFrame netexFareFrame, EntitiesEvictor evictor) {

        boolean exportGroupOfTariffZones = false;

        Iterator<org.rutebanken.tiamat.model.FareZone> fareZoneIterator;
        if (exportParams.getFareZoneExportMode() == null || exportParams.getFareZoneExportMode().equals(ExportParams.ExportMode.ALL)) {
            logger.info("Preparing to scroll fare zones, regardless of version");
            fareZoneIterator = fareZoneRepository.scrollFareZones(exportParams);
            exportGroupOfTariffZones = true;
        } else if (exportParams.getFareZoneExportMode().equals(ExportParams.ExportMode.RELEVANT)) {
            if (!stopPlacePrimaryIds.isEmpty()) {
                int fareZoneCount = fareZoneRepository.countResult(stopPlacePrimaryIds);
                logger.info("Preparing to scroll {} relevant fare zones from stop place ids", fareZoneCount);
                fareZoneIterator = fareZoneRepository.scrollFareZones(stopPlacePrimaryIds);

                exportGroupOfTariffZones = true;
            } else {
                logger.info("No stop places to export");
                fareZoneIterator = Collections.emptyIterator();
            }
        } else {
            logger.info("Fare zone export mode is {}. Will not export fare zones", exportParams.getFareZoneExportMode());
            fareZoneIterator = Collections.emptyIterator();
        }
        var fareZonesInFrameRelStructure = new FareZonesInFrame_RelStructure();
        List<FareZone> netexFareZones= new ArrayList<>();
        while (fareZoneIterator.hasNext()) {
            final org.rutebanken.tiamat.model.FareZone fareZone = fareZoneIterator.next();
            final FareZone netexFareZone = netexMapper.mapToNetexModel(fareZone);
            netexFareZones.add(netexFareZone);
            mappedFareZonesCount.incrementAndGet();

        }
        if (!netexFareZones.isEmpty()){
            setField(FareZonesInFrame_RelStructure.class, "fareZone", fareZonesInFrameRelStructure, netexFareZones);
            netexFareFrame.setFareZones(fareZonesInFrameRelStructure);
            }

        if (exportGroupOfTariffZones) {
            prepareGroupOfTariffZones(exportParams,stopPlacePrimaryIds,mappedGroupOfTariffZonesCount,netexSiteFrame,evictor);
        }
    }

    private void prepareTariffZones(ExportParams exportParams, Set<Long> stopPlacePrimaryIds, AtomicInteger mappedTariffZonesCount, SiteFrame netexSiteFrame, EntitiesEvictor evicter) {


        Iterator<org.rutebanken.tiamat.model.TariffZone> tariffZoneIterator;
        if (exportParams.getTariffZoneExportMode() == null || exportParams.getTariffZoneExportMode().equals(ExportParams.ExportMode.ALL)) {

            logger.info("Preparing to scroll all tariff zones, regardless of version");
            tariffZoneIterator = tariffZoneRepository.scrollTariffZones(exportParams);
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
            List<PassengerStopAssignment> netexStopAssignments = new ArrayList<>();

            while (parentStopFetchingIterator.hasNext()) {
                final org.rutebanken.tiamat.model.StopPlace stopPlace = parentStopFetchingIterator.next();
                this.serviceFrameElementCreator.createServiceFrameElements(stopPlace, netexScheduledStopPoints, netexStopAssignments, null);
            }

            List<JAXBElement<PassengerStopAssignment>> stopAssignments = netexStopAssignments.stream().map(s -> new ObjectFactory().createPassengerStopAssignment(s)).toList();

            if (!netexScheduledStopPoints.isEmpty()) {
                final ScheduledStopPointsInFrame_RelStructure scheduledStopPointsInFrame_relStructure = new ScheduledStopPointsInFrame_RelStructure();
                setField(ScheduledStopPointsInFrame_RelStructure.class, "scheduledStopPoint", scheduledStopPointsInFrame_relStructure, netexScheduledStopPoints);
                netexServiceFrame.setScheduledStopPoints(scheduledStopPointsInFrame_relStructure);

                StopAssignmentsInFrame_RelStructure stopAssignmentsInFrame_RelStructure = new StopAssignmentsInFrame_RelStructure();
                setField(StopAssignmentsInFrame_RelStructure.class, "stopAssignment", stopAssignmentsInFrame_RelStructure, stopAssignments);
                netexServiceFrame.setStopAssignments(stopAssignmentsInFrame_RelStructure);
            }
        }
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

    private void prepareGroupOfStopPlaces(ExportParams exportParams, Set<Long> stopPlacePrimaryIds, AtomicInteger mappedGroupOfStopPlacesCount, SiteFrame netexSiteFrame, org.rutebanken.netex.model.ResourceFrame netexResourceFrame, EntitiesEvictor evicter) {

        Iterator<GroupOfStopPlaces> groupOfStopPlacesIterator;

        Iterator<PurposeOfGrouping> purposeOfGroupingIterator = purposeOfGroupingRepository.findAllPurposeOfGrouping().listIterator();

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

            if (purposeOfGroupingIterator.hasNext()){
                NetexMappingIterator<PurposeOfGrouping, org.rutebanken.netex.model.PurposeOfGrouping>  pogNetexMapingIterator = new NetexMappingIterator<>(
                        netexMapper,purposeOfGroupingIterator,org.rutebanken.netex.model.PurposeOfGrouping.class,new AtomicInteger(),evicter);
                final NetexMappingIteratorList<org.rutebanken.netex.model.PurposeOfGrouping> netexPurposeOfGroupingList = new NetexMappingIteratorList<>(() -> pogNetexMapingIterator);
                List<JAXBElement<? extends DataManagedObjectStructure>> netexPurposeOfGroupingList2 = new ArrayList<>();
                for( org.rutebanken.netex.model.PurposeOfGrouping netexPurposeOfGrouping: netexPurposeOfGroupingList) {
                    final JAXBElement<org.rutebanken.netex.model.PurposeOfGrouping> purposeOfGrouping2= new ObjectFactory().createPurposeOfGrouping(netexPurposeOfGrouping);
                    netexPurposeOfGroupingList2.add(purposeOfGrouping2);
                }

                netexResourceFrame.withTypesOfValue(new ObjectFactory()
                        .createTypesOfValueInFrame_RelStructure().withValueSetOrTypeOfValue(netexPurposeOfGroupingList2));
            }
        } else {
            netexSiteFrame.setGroupsOfStopPlaces(null);
        }
    }

    private void prepareGroupOfTariffZones(ExportParams exportParams, Set<Long> stopPlaceIds, AtomicInteger mappedGroupOfTariffZonesCount, SiteFrame netexSiteFrame, EntitiesEvictor evicter) {
        Iterator<GroupOfTariffZones> groupOfTariffZonesIterator;
        if (exportParams.getGroupOfTariffZonesExportMode() == null || exportParams.getGroupOfTariffZonesExportMode().equals(ExportParams.ExportMode.ALL)) {
            logger.info("Prepare scrolling for all group of tariff zones");
            groupOfTariffZonesIterator = groupOfTariffZonesRepository.scrollGroupOfTariffZones();
        } else if (exportParams.getGroupOfTariffZonesExportMode().equals(ExportParams.ExportMode.RELEVANT)) {
            logger.info("Prepare scrolling relevant group of tariff zones");
            groupOfTariffZonesIterator = groupOfTariffZonesRepository.scrollGroupOfTariffZones(stopPlaceIds);

        } else {
            logger.info("Group of tariff zones export mode is {}. Will not export group of tariff zones", exportParams.getGroupOfStopPlacesExportMode());
            groupOfTariffZonesIterator = Collections.emptyIterator();
        }

        if (groupOfTariffZonesIterator.hasNext()) {
            NetexMappingIterator<GroupOfTariffZones, org.rutebanken.netex.model.GroupOfTariffZones> netexMappingIterator = new NetexMappingIterator<>(
                    netexMapper, groupOfTariffZonesIterator, org.rutebanken.netex.model.GroupOfTariffZones.class,mappedGroupOfTariffZonesCount,evicter);

            List<org.rutebanken.netex.model.GroupOfTariffZones> groupOfTariffZonesList = new NetexMappingIteratorList<>(() -> netexMappingIterator);

            final GroupsOfTariffZonesInFrame_RelStructure groupsOfTariffZonesInFrame_relStructure = new GroupsOfTariffZonesInFrame_RelStructure();
            setField(GroupsOfTariffZonesInFrame_RelStructure.class,"groupOfTariffZones", groupsOfTariffZonesInFrame_relStructure,groupOfTariffZonesList);
            netexSiteFrame.setGroupsOfTariffZones(groupsOfTariffZonesInFrame_relStructure);
        }

    }

    private EntitiesEvictor instantiateEvictor() {
        if (entityManager != null) {
            SessionImpl currentSession = entityManager.unwrap(SessionImpl.class);
            return new SessionEntitiesEvictor(currentSession);
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
