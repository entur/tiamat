package org.rutebanken.tiamat.importer;

import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.importer.filter.ZoneTopographicPlaceFilter;
import org.rutebanken.tiamat.importer.handler.StopPlaceImportHandler;
import org.rutebanken.tiamat.importer.initial.ParallelInitialParkingImporter;
import org.rutebanken.tiamat.importer.log.ImportLogger;
import org.rutebanken.tiamat.importer.log.ImportLoggerTask;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PublicationDeliveryImporter {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryImporter.class);

    public static final String IMPORT_CORRELATION_ID = "importCorrelationId";
    private static final Object PARKING_IMPORT_LOCK = new Object();

    private final PublicationDeliveryHelper publicationDeliveryHelper;
    private final TransactionalParkingsImporter transactionalParkingsImporter;
    private final PublicationDeliveryExporter publicationDeliveryExporter;
    private final NetexMapper netexMapper;
    private final PathLinksImporter pathLinksImporter;
    private final TopographicPlaceImporter topographicPlaceImporter;
    private final TariffZoneImporter tariffZoneImporter;
    private final ZoneTopographicPlaceFilter zoneTopographicPlaceFilter;
    private final ParallelInitialParkingImporter parallelInitialParkingImporter;
    private final StopPlaceImportHandler stopPlaceImportHandler;

    @Autowired
    public PublicationDeliveryImporter(PublicationDeliveryHelper publicationDeliveryHelper, NetexMapper netexMapper,
                                       TransactionalParkingsImporter transactionalParkingsImporter,
                                       PublicationDeliveryExporter publicationDeliveryExporter,
                                       PathLinksImporter pathLinksImporter,
                                       TopographicPlaceImporter topographicPlaceImporter,
                                       TariffZoneImporter tariffZoneImporter,
                                       ZoneTopographicPlaceFilter zoneTopographicPlaceFilter,
                                       ParallelInitialParkingImporter parallelInitialParkingImporter,
                                       StopPlaceImportHandler stopPlaceImportHandler) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.netexMapper = netexMapper;
        this.transactionalParkingsImporter = transactionalParkingsImporter;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.pathLinksImporter = pathLinksImporter;
        this.topographicPlaceImporter = topographicPlaceImporter;
        this.tariffZoneImporter = tariffZoneImporter;
        this.zoneTopographicPlaceFilter = zoneTopographicPlaceFilter;
        this.parallelInitialParkingImporter = parallelInitialParkingImporter;
        this.stopPlaceImportHandler = stopPlaceImportHandler;
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

        if (publicationDeliveryParams == null) {
            publicationDeliveryParams = new PublicationDeliveryParams();
        } else {
            validate(publicationDeliveryParams);
        }

        logger.info("Got publication delivery with {} site frames", incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame().size());

        AtomicInteger stopPlacesCreatedOrUpdated = new AtomicInteger(0);
        AtomicInteger parkingsCreatedOrUpdated = new AtomicInteger(0);
        AtomicInteger topographicPlacesCounter = new AtomicInteger(0);
        SiteFrame netexSiteFrame = publicationDeliveryHelper.findSiteFrame(incomingPublicationDelivery);

        String requestId = netexSiteFrame.getId();

        Timer loggerTimer = new ImportLogger(new ImportLoggerTask(stopPlacesCreatedOrUpdated, publicationDeliveryHelper.numberOfStops(netexSiteFrame), topographicPlacesCounter, netexSiteFrame.getId()));

        try {
            SiteFrame responseSiteframe = new SiteFrame();

            MDC.put(IMPORT_CORRELATION_ID, requestId);
            logger.info("Publication delivery contains site frame created at {}", netexSiteFrame.getCreated());

            responseSiteframe.withId(requestId + "-response").withVersion("1");

            if (publicationDeliveryHelper.hasTopographicPlaces(netexSiteFrame)) {
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


            if (publicationDeliveryHelper.hasTariffZones(netexSiteFrame) && publicationDeliveryParams.importType != ImportType.ID_MATCH) {
                List<org.rutebanken.tiamat.model.TariffZone> tiamatTariffZones = netexMapper.getFacade().mapAsList(netexSiteFrame.getTariffZones().getTariffZone(), org.rutebanken.tiamat.model.TariffZone.class);
                logger.debug("Mapped {} tariff zones from netex to internal model", tiamatTariffZones.size());
                List<TariffZone> importedTariffZones = tariffZoneImporter.importTariffZones(tiamatTariffZones);
                logger.debug("Got {} imported tariffZones ", importedTariffZones.size());
                if (!importedTariffZones.isEmpty()) {
                    responseSiteframe.withTariffZones(new TariffZonesInFrame_RelStructure().withTariffZone(importedTariffZones));
                }
            }

            stopPlaceImportHandler.handleStops(netexSiteFrame, publicationDeliveryParams, stopPlacesCreatedOrUpdated, responseSiteframe);
            handleParkings(netexSiteFrame, publicationDeliveryParams, parkingsCreatedOrUpdated, responseSiteframe);

            if (netexSiteFrame.getPathLinks() != null && netexSiteFrame.getPathLinks().getPathLink() != null) {
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

        if (publicationDeliveryHelper.hasParkings(netexSiteFrame)) {

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

            if (publicationDeliveryParams.importType == null || publicationDeliveryParams.importType.equals(ImportType.MERGE)) {
                synchronized (PARKING_IMPORT_LOCK) {
                    importedParkings = transactionalParkingsImporter.importParkings(tiamatParking, parkingsCreatedOrUpdated);
                }
            } else if (publicationDeliveryParams.importType.equals(ImportType.INITIAL)) {
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

    private void validate(PublicationDeliveryParams publicationDeliveryParams) {
        if (publicationDeliveryParams.targetTopographicPlaces != null && publicationDeliveryParams.onlyMatchOutsideTopographicPlaces != null) {
            if (!publicationDeliveryParams.targetTopographicPlaces.isEmpty() && !publicationDeliveryParams.onlyMatchOutsideTopographicPlaces.isEmpty()) {
                throw new IllegalArgumentException("targetTopographicPlaces and targetTopographicPlaces cannot be specified at the same time!");
            }
        }
    }

}
