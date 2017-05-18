package org.rutebanken.tiamat.importer;

import org.apache.commons.lang.NotImplementedException;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.exporter.TariffZonesFromStopsExporter;
import org.rutebanken.tiamat.exporter.TopographicPlacesExporter;
import org.rutebanken.tiamat.importer.filter.ZoneTopographicPlaceFilter;
import org.rutebanken.tiamat.importer.initial.ParallelInitialParkingImporter;
import org.rutebanken.tiamat.importer.initial.ParallelInitialStopPlaceImporter;
import org.rutebanken.tiamat.importer.log.ImportLogger;
import org.rutebanken.tiamat.importer.log.ImportLoggerTask;
import org.rutebanken.tiamat.importer.modifier.StopPlacePostFilterSteps;
import org.rutebanken.tiamat.importer.modifier.StopPlacePreSteps;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PublicationDeliveryImporter {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryImporter.class);

    public static final String IMPORT_CORRELATION_ID = "importCorrelationId";
    private static final Object STOP_PLACE_IMPORT_LOCK = new Object();
    private static final Object PARKING_IMPORT_LOCK = new Object();

    /**
     * Make this configurable. Export topographic places in response.
     */
    private static final boolean EXPORT_TOPOGRAPHIC_PLACES_FOR_STOPS = false;

    private final TransactionalStopPlacesImporter transactionalStopPlacesImporter;
    private final TransactionalParkingsImporter transactionalParkingsImporter;
    private final PublicationDeliveryExporter publicationDeliveryExporter;
    private final NetexMapper netexMapper;
    private final StopPlacePreSteps stopPlacePreSteps;
    private final StopPlacePostFilterSteps stopPlacePostFilterSteps;
    private final PathLinksImporter pathLinksImporter;
    private final TopographicPlacesExporter topographicPlacesExporter;
    private final TopographicPlaceImporter topographicPlaceImporter;
    private final TariffZoneImporter tariffZoneImporter;
    private final ZoneTopographicPlaceFilter zoneTopographicPlaceFilter;
    private final ParallelInitialStopPlaceImporter parallelInitialStopPlaceImporter;
    private final ParallelInitialParkingImporter parallelInitialParkingImporter;
    private final MatchingAppendingIdStopPlacesImporter matchingAppendingIdStopPlacesImporter;
    private final TariffZonesFromStopsExporter tariffZonesFromStopsExporter;
    private final StopPlaceIdMatcher stopPlaceIdMatcher;

    @Autowired
    public PublicationDeliveryImporter(NetexMapper netexMapper,
                                       TransactionalStopPlacesImporter transactionalStopPlacesImporter,
                                       TransactionalParkingsImporter transactionalParkingsImporter,
                                       PublicationDeliveryExporter publicationDeliveryExporter,
                                       StopPlacePreSteps stopPlacePreSteps,
                                       StopPlacePostFilterSteps stopPlacePostFilterSteps, PathLinksImporter pathLinksImporter,
                                       TopographicPlacesExporter topographicPlacesExporter,
                                       TopographicPlaceImporter topographicPlaceImporter,
                                       TariffZoneImporter tariffZoneImporter,
                                       ZoneTopographicPlaceFilter zoneTopographicPlaceFilter,
                                       ParallelInitialStopPlaceImporter parallelInitialStopPlaceImporter,
                                       ParallelInitialParkingImporter parallelInitialParkingImporter,
                                       MatchingAppendingIdStopPlacesImporter matchingAppendingIdStopPlacesImporter,
                                       TariffZonesFromStopsExporter tariffZonesFromStopsExporter, StopPlaceIdMatcher stopPlaceIdMatcher) {
        this.netexMapper = netexMapper;
        this.transactionalStopPlacesImporter = transactionalStopPlacesImporter;
        this.transactionalParkingsImporter = transactionalParkingsImporter;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.stopPlacePreSteps = stopPlacePreSteps;
        this.stopPlacePostFilterSteps = stopPlacePostFilterSteps;
        this.pathLinksImporter = pathLinksImporter;
        this.topographicPlacesExporter = topographicPlacesExporter;
        this.topographicPlaceImporter = topographicPlaceImporter;
        this.tariffZoneImporter = tariffZoneImporter;
        this.zoneTopographicPlaceFilter = zoneTopographicPlaceFilter;
        this.parallelInitialStopPlaceImporter = parallelInitialStopPlaceImporter;
        this.parallelInitialParkingImporter = parallelInitialParkingImporter;
        this.matchingAppendingIdStopPlacesImporter = matchingAppendingIdStopPlacesImporter;
        this.tariffZonesFromStopsExporter = tariffZonesFromStopsExporter;
        this.stopPlaceIdMatcher = stopPlaceIdMatcher;
    }


    public PublicationDeliveryStructure importPublicationDelivery(PublicationDeliveryStructure incomingPublicationDelivery) {
        return importPublicationDelivery(incomingPublicationDelivery, null);
    }

    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure importPublicationDelivery(PublicationDeliveryStructure incomingPublicationDelivery, PublicationDeliveryParams publicationDeliveryParams) {
        if (incomingPublicationDelivery.getDataObjects() == null) {
            String responseMessage = "Received publication delivery but it does not contain any data objects.";
            logger.warn(responseMessage);
            throw new RuntimeException(responseMessage);
        }

        if(publicationDeliveryParams == null) {
            publicationDeliveryParams = new PublicationDeliveryParams();
        } else {
            validate(publicationDeliveryParams);
        }

        logger.info("Got publication delivery with {} site frames", incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame().size());

        AtomicInteger stopPlacesCreatedOrUpdated = new AtomicInteger(0);
        AtomicInteger parkingsCreatedOrUpdated = new AtomicInteger(0);
        AtomicInteger topographicPlacesCounter = new AtomicInteger(0);
        SiteFrame netexSiteFrame = findSiteFrame(incomingPublicationDelivery);

        String requestId = netexSiteFrame.getId();

        Timer loggerTimer = new ImportLogger(new ImportLoggerTask(stopPlacesCreatedOrUpdated, numberOfStops(netexSiteFrame), topographicPlacesCounter, netexSiteFrame.getId()));

        try {
            SiteFrame responseSiteframe = new SiteFrame();

            MDC.put(IMPORT_CORRELATION_ID, requestId);
            logger.info("Publication delivery contains site frame created at {}", netexSiteFrame.getCreated());

            responseSiteframe.withId(requestId + "-response").withVersion("1");

            if(hasTopographicPlaces(netexSiteFrame)) {
                logger.info("Publication delivery contains {} topographic places for import.", netexSiteFrame.getTopographicPlaces().getTopographicPlace().size());

                logger.info("About to map {} topographic places to internal model", netexSiteFrame.getTopographicPlaces().getTopographicPlace().size());
                List<org.rutebanken.tiamat.model.TopographicPlace> mappedTopographicPlaces = netexMapper.getFacade()
                        .mapAsList(netexSiteFrame.getTopographicPlaces().getTopographicPlace(),
                                org.rutebanken.tiamat.model.TopographicPlace.class);
                logger.info("Mapped {} topographic places to internal model", mappedTopographicPlaces.size());
                List<TopographicPlace> importedTopographicPlaces = topographicPlaceImporter.importTopographicPlaces(mappedTopographicPlaces, topographicPlacesCounter);
                responseSiteframe.withTopographicPlaces(new TopographicPlacesInFrame_RelStructure().withTopographicPlace(importedTopographicPlaces));
                logger.info("Finished importing topographic places");
            }


            if(hasTariffZones(netexSiteFrame) && publicationDeliveryParams.importType != ImportType.ID_MATCH) {
                List<org.rutebanken.tiamat.model.TariffZone> tiamatTariffZones = netexMapper.getFacade().mapAsList(netexSiteFrame.getTariffZones().getTariffZone(), org.rutebanken.tiamat.model.TariffZone.class);
                logger.debug("Mapped {} tariff zones from netex to internal model", tiamatTariffZones.size());
                List<TariffZone> importedTariffZones = tariffZoneImporter.importTariffZones(tiamatTariffZones);
                logger.debug("Got {} imported tariffZones ", importedTariffZones.size());
                if(!importedTariffZones.isEmpty()) {
                    responseSiteframe.withTariffZones(new TariffZonesInFrame_RelStructure().withTariffZone(importedTariffZones));
                }
            }

            handleStops(netexSiteFrame, publicationDeliveryParams, stopPlacesCreatedOrUpdated, responseSiteframe);
            handleParkings(netexSiteFrame, publicationDeliveryParams, parkingsCreatedOrUpdated, responseSiteframe);

            if(netexSiteFrame.getPathLinks() != null && netexSiteFrame.getPathLinks().getPathLink() != null) {
                List<org.rutebanken.tiamat.model.PathLink> tiamatPathLinks = netexMapper.mapPathLinksToTiamatModel(netexSiteFrame.getPathLinks().getPathLink());
                tiamatPathLinks.forEach(tiamatPathLink -> logger.debug("Received path link: {}", tiamatPathLink));

                List<org.rutebanken.netex.model.PathLink> pathLinks = pathLinksImporter.importPathLinks(tiamatPathLinks);
                responseSiteframe.withPathLinks(new PathLinksInFrame_RelStructure().withPathLink(pathLinks));
            }


            return publicationDeliveryExporter.exportSiteFrame(responseSiteframe);
        } finally {
            MDC.remove(IMPORT_CORRELATION_ID);
            loggerTimer.cancel();
        }
    }

    private void handleParkings(SiteFrame netexSiteFrame, PublicationDeliveryParams publicationDeliveryParams, AtomicInteger parkingsCreatedOrUpdated, SiteFrame responseSiteframe) {

        if (hasParkings(netexSiteFrame)) {

            List<org.rutebanken.tiamat.model.Parking> tiamatParking = netexMapper.mapParkingsToTiamatModel(netexSiteFrame.getParkings().getParking());

            int numberOfParkingsBeforeFiltering = tiamatParking.size();
            logger.info("About to filter {} parkings based on topographic references: {}", tiamatParking.size(), publicationDeliveryParams.targetTopographicPlaces);
            tiamatParking = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(publicationDeliveryParams.targetTopographicPlaces, tiamatParking);
            logger.info("Got {} parkings (was {}) after filtering by: {}", tiamatParking.size(), numberOfParkingsBeforeFiltering, publicationDeliveryParams.targetTopographicPlaces);

            if (publicationDeliveryParams.onlyMatchOutsideTopographicPlaces != null && !publicationDeliveryParams.onlyMatchOutsideTopographicPlaces.isEmpty()) {
                numberOfParkingsBeforeFiltering = tiamatParking.size();
                logger.info("Filtering parkings outside given list of topographic places: {}", publicationDeliveryParams.onlyMatchOutsideTopographicPlaces);
                tiamatParking = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(publicationDeliveryParams.onlyMatchOutsideTopographicPlaces, tiamatParking, true);
                logger.info("Got {} parkings (was {}) after filtering", tiamatParking.size(), numberOfParkingsBeforeFiltering);
            }


            Collection<Parking> importedParkings;

            if(publicationDeliveryParams.importType == null || publicationDeliveryParams.importType.equals(ImportType.MERGE)) {
                synchronized (PARKING_IMPORT_LOCK) {
                    importedParkings = transactionalParkingsImporter.importParkings(tiamatParking, parkingsCreatedOrUpdated);
                }
            } else if(publicationDeliveryParams.importType.equals(ImportType.INITIAL)) {
                importedParkings = parallelInitialParkingImporter.importParkings(tiamatParking, parkingsCreatedOrUpdated);
            } else {
                logger.warn("Import type " + publicationDeliveryParams.importType + " not implemented. Will not match parking.");
                importedParkings = new ArrayList<>(0);
            }

            if (!importedParkings.isEmpty()) {
                responseSiteframe.withParkings(
                        new ParkingsInFrame_RelStructure()
                                .withParking(importedParkings));
            }

            logger.info("Mapped {} parkings!!", tiamatParking.size());

        }
    }


    private void handleStops(SiteFrame netexSiteFrame, PublicationDeliveryParams publicationDeliveryParams, AtomicInteger stopPlacesCreatedMatchedOrUpdated, SiteFrame responseSiteframe) {
        if(hasStops(netexSiteFrame)) {
            List<org.rutebanken.tiamat.model.StopPlace> tiamatStops = netexMapper.mapStopsToTiamatModel(netexSiteFrame.getStopPlaces().getStopPlace());
            logger.info("Running stop place pre steps");
            tiamatStops = stopPlacePreSteps.run(tiamatStops);

            int numberOfStopBeforeFiltering = tiamatStops.size();
            logger.info("About to filter {} stops based on topographic references: {}", tiamatStops.size(), publicationDeliveryParams.targetTopographicPlaces);
            tiamatStops = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(publicationDeliveryParams.targetTopographicPlaces, tiamatStops);
            logger.info("Got {} stops (was {}) after filtering by: {}", tiamatStops.size(), numberOfStopBeforeFiltering, publicationDeliveryParams.targetTopographicPlaces);

            if (publicationDeliveryParams.onlyMatchOutsideTopographicPlaces != null && !publicationDeliveryParams.onlyMatchOutsideTopographicPlaces.isEmpty()) {
                numberOfStopBeforeFiltering = tiamatStops.size();
                logger.info("Filtering stops outside given list of topographic places: {}", publicationDeliveryParams.onlyMatchOutsideTopographicPlaces);
                tiamatStops = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(publicationDeliveryParams.onlyMatchOutsideTopographicPlaces, tiamatStops, true);
                logger.info("Got {} stops (was {}) after filtering", tiamatStops.size(), numberOfStopBeforeFiltering);
            }

            logger.info("Running stop place post filter steps");
            tiamatStops = stopPlacePostFilterSteps.run(tiamatStops);

            final Collection<org.rutebanken.netex.model.StopPlace> importedOrMatchedNetexStopPlaces;
            logger.info("The import type is: {}", publicationDeliveryParams.importType);

            synchronized (STOP_PLACE_IMPORT_LOCK) {
                if (publicationDeliveryParams.importType == null || publicationDeliveryParams.importType.equals(ImportType.MERGE)) {
                    importedOrMatchedNetexStopPlaces = transactionalStopPlacesImporter.importStopPlaces(tiamatStops, stopPlacesCreatedMatchedOrUpdated);
                } else if (publicationDeliveryParams.importType.equals(ImportType.INITIAL)) {
                    importedOrMatchedNetexStopPlaces = parallelInitialStopPlaceImporter.importStopPlaces(tiamatStops, stopPlacesCreatedMatchedOrUpdated);
                } else if (publicationDeliveryParams.importType.equals(ImportType.MATCH)) {
                    importedOrMatchedNetexStopPlaces = matchingAppendingIdStopPlacesImporter.importStopPlaces(tiamatStops, stopPlacesCreatedMatchedOrUpdated);
                } else if(publicationDeliveryParams.importType.equals(ImportType.ID_MATCH)) {
                    importedOrMatchedNetexStopPlaces = stopPlaceIdMatcher.matchStopPlaces(tiamatStops, stopPlacesCreatedMatchedOrUpdated);
                } else {
                    throw new NotImplementedException("Import type " + publicationDeliveryParams.importType + " not implemented ");
                }
            }
            logger.info("Imported/matched/updated {} stop places", stopPlacesCreatedMatchedOrUpdated);

            tariffZonesFromStopsExporter.resolveTariffZones(importedOrMatchedNetexStopPlaces, responseSiteframe);

            if(responseSiteframe.getTariffZones() != null
                    && responseSiteframe.getTariffZones().getTariffZone() != null
                    && responseSiteframe.getTariffZones().getTariffZone().isEmpty()) {
                responseSiteframe.setTariffZones(null);
            }

            if (EXPORT_TOPOGRAPHIC_PLACES_FOR_STOPS) {
                List<TopographicPlace> netexTopographicPlaces = topographicPlacesExporter.export(findTopographicPlaceRefsFromStops(tiamatStops));

                if (!netexTopographicPlaces.isEmpty()) {
                    responseSiteframe.withTopographicPlaces(
                            new TopographicPlacesInFrame_RelStructure()
                                    .withTopographicPlace(netexTopographicPlaces));
                }
            } else {
                clearTopographicPlaceRefs(importedOrMatchedNetexStopPlaces);
            }

            if (!importedOrMatchedNetexStopPlaces.isEmpty()) {
                logger.info("Add {} stops to response site frame", importedOrMatchedNetexStopPlaces.size());
                responseSiteframe.withStopPlaces(
                        new StopPlacesInFrame_RelStructure()
                                .withStopPlace(importedOrMatchedNetexStopPlaces));
            } else {
                logger.info("No stops in response");
            }
        }
    }

    private boolean hasTariffZones(SiteFrame netexSiteFrame) {
        return netexSiteFrame.getTariffZones() != null && netexSiteFrame.getTariffZones().getTariffZone() != null;
    }

    private void validate(PublicationDeliveryParams publicationDeliveryParams) {
        if(publicationDeliveryParams.targetTopographicPlaces != null && publicationDeliveryParams.onlyMatchOutsideTopographicPlaces != null) {
            if(!publicationDeliveryParams.targetTopographicPlaces.isEmpty() && ! publicationDeliveryParams.onlyMatchOutsideTopographicPlaces.isEmpty()) {
                throw new IllegalArgumentException("targetTopographicPlaces and targetTopographicPlaces cannot be specified at the same time!");
            }
        }
    }

    public void clearTopographicPlaceRefs(Collection<StopPlace> stopPlaces) {
        stopPlaces.stream().forEach(stopPlace -> stopPlace.setTopographicPlaceRef(null));
    }

    public List<Pair<String, Long>> findTopographicPlaceRefsFromStops(Collection<org.rutebanken.tiamat.model.StopPlace> stopPlaces) {
        return stopPlaces
                .stream()
                .filter(stopPlace -> stopPlace.getTopographicPlace() != null)
                .map(org.rutebanken.tiamat.model.StopPlace::getTopographicPlace)
                .map(topographicPlace -> Pair.of(topographicPlace.getNetexId(), topographicPlace.getVersion()))
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean hasTopographicPlaces(SiteFrame netexSiteFrame) {
        return netexSiteFrame.getTopographicPlaces() != null
                && netexSiteFrame.getTopographicPlaces().getTopographicPlace() != null
                && !netexSiteFrame.getTopographicPlaces().getTopographicPlace().isEmpty();
    }

    private boolean hasStops(SiteFrame siteFrame) {
        return siteFrame.getStopPlaces() != null && siteFrame.getStopPlaces().getStopPlace() != null;
    }

    private boolean hasParkings(SiteFrame siteFrame) {
        return siteFrame.getParkings() != null && siteFrame.getParkings().getParking() != null;
    }

    private int numberOfStops(SiteFrame netexSiteFrame) {
        return hasStops(netexSiteFrame) ? netexSiteFrame.getStopPlaces().getStopPlace().size() : 0;
    }

    public SiteFrame findSiteFrame(PublicationDeliveryStructure incomingPublicationDelivery) {

        List<JAXBElement<? extends Common_VersionFrameStructure>> compositeFrameOrCommonFrame = incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame();

        Optional<SiteFrame> optionalSiteframe = compositeFrameOrCommonFrame
                .stream()
                .filter(element -> element.getValue() instanceof SiteFrame)
                .map(element -> (SiteFrame) element.getValue())
                .findFirst();

        if (optionalSiteframe.isPresent()) {
            return optionalSiteframe.get();
        }

        return compositeFrameOrCommonFrame
                .stream()
                .filter(element -> element.getValue() instanceof CompositeFrame)
                .map(element -> (CompositeFrame) element.getValue())
                .map(compositeFrame -> compositeFrame.getFrames())
                .flatMap(frames -> frames.getCommonFrame().stream())
                .filter(jaxbElement -> jaxbElement.getValue() instanceof SiteFrame)
                .map(jaxbElement -> (SiteFrame) jaxbElement.getValue())
                .findAny().get();
    }
}
