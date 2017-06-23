package org.rutebanken.tiamat.importer;

import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.importer.handler.ParkingsImportHandler;
import org.rutebanken.tiamat.importer.handler.StopPlaceImportHandler;
import org.rutebanken.tiamat.importer.log.ImportLogger;
import org.rutebanken.tiamat.importer.log.ImportLoggerTask;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PublicationDeliveryImporter {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryImporter.class);

    public static final String IMPORT_CORRELATION_ID = "importCorrelationId";

    private final PublicationDeliveryHelper publicationDeliveryHelper;
    private final PublicationDeliveryExporter publicationDeliveryExporter;
    private final NetexMapper netexMapper;
    private final PathLinksImporter pathLinksImporter;
    private final TopographicPlaceImporter topographicPlaceImporter;
    private final TariffZoneImporter tariffZoneImporter;
    private final StopPlaceImportHandler stopPlaceImportHandler;
    private final ParkingsImportHandler parkingsImportHandler;

    @Autowired
    public PublicationDeliveryImporter(PublicationDeliveryHelper publicationDeliveryHelper, NetexMapper netexMapper,
                                       PublicationDeliveryExporter publicationDeliveryExporter,
                                       PathLinksImporter pathLinksImporter,
                                       TopographicPlaceImporter topographicPlaceImporter,
                                       TariffZoneImporter tariffZoneImporter,
                                       StopPlaceImportHandler stopPlaceImportHandler,
                                       ParkingsImportHandler parkingsImportHandler) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.netexMapper = netexMapper;
        this.parkingsImportHandler = parkingsImportHandler;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.pathLinksImporter = pathLinksImporter;
        this.topographicPlaceImporter = topographicPlaceImporter;
        this.tariffZoneImporter = tariffZoneImporter;
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
            parkingsImportHandler.handleParkings(netexSiteFrame, publicationDeliveryParams, parkingsCreatedOrUpdated, responseSiteframe);

            if (netexSiteFrame.getPathLinks() != null && netexSiteFrame.getPathLinks().getPathLink() != null) {
                List<org.rutebanken.tiamat.model.PathLink> tiamatPathLinks = netexMapper.mapPathLinksToTiamatModel(netexSiteFrame.getPathLinks().getPathLink());
                tiamatPathLinks.forEach(tiamatPathLink -> logger.debug("Received path link: {}", tiamatPathLink));

                List<org.rutebanken.netex.model.PathLink> pathLinks = pathLinksImporter.importPathLinks(tiamatPathLinks);
                responseSiteframe.withPathLinks(new PathLinksInFrame_RelStructure().withPathLink(pathLinks));
            }

            return publicationDeliveryExporter.createPublicationDelivery(responseSiteframe);
        } finally {
            MDC.remove(IMPORT_CORRELATION_ID);
            loggerTimer.cancel();
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
