package org.rutebanken.tiamat.importer;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.importer.log.ImportLogger;
import org.rutebanken.tiamat.importer.log.ImportLoggerTask;
import org.rutebanken.tiamat.importer.modifier.StopPlacePreSteps;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PublicationDeliveryImporter {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryImporter.class);

    public static final String IMPORT_CORRELATION_ID = "importCorrelationId";
    private static final Object STOP_PLACE_IMPORT_LOCK = new Object();

    private final StopPlaceImporter stopPlaceImporter;
    private final SiteFrameImporter siteFrameImporter;
    private final PublicationDeliveryExporter publicationDeliveryExporter;
    private final NetexMapper netexMapper;
    private final StopPlacePreSteps stopPlacePreSteps;
    private final PathLinksImporter pathLinksImporter;


    @Autowired
    public PublicationDeliveryImporter(NetexMapper netexMapper,
                                       @Qualifier("mergingStopPlaceImporter") StopPlaceImporter stopPlaceImporter,
                                       SiteFrameImporter siteFrameImporter, PublicationDeliveryExporter publicationDeliveryExporter, StopPlacePreSteps stopPlacePreSteps, PathLinksImporter pathLinksImporter) {
        this.netexMapper = netexMapper;

        this.stopPlaceImporter = stopPlaceImporter;
        this.siteFrameImporter = siteFrameImporter;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.stopPlacePreSteps = stopPlacePreSteps;
        this.pathLinksImporter = pathLinksImporter;
    }


    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure importPublicationDelivery(PublicationDeliveryStructure incomingPublicationDelivery) {
        if (incomingPublicationDelivery.getDataObjects() == null) {
            String responseMessage = "Received publication delivery but it does not contain any data objects.";
            logger.warn(responseMessage);
            throw new RuntimeException(responseMessage);
        }
        logger.info("Got publication delivery with {} site frames", incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame().size());

        AtomicInteger stopPlacesCreated = new AtomicInteger(0);
        AtomicInteger topographicPlacesCounter = new AtomicInteger(0);

        SiteFrame netexSiteFrame = findSiteFrame(incomingPublicationDelivery);

        Timer loggerTimer = new ImportLogger(new ImportLoggerTask(stopPlacesCreated, numberOfStops(netexSiteFrame), topographicPlacesCounter, netexSiteFrame.getId()));

        try {

            MDC.put(IMPORT_CORRELATION_ID, netexSiteFrame.getId());
            logger.info("Publication delivery contains site frame created at {}", netexSiteFrame.getCreated());

            List<StopPlace> tiamatStops = netexMapper.mapToTiamatModel(netexSiteFrame.getStopPlaces().getStopPlace());
            tiamatStops = stopPlacePreSteps.run(tiamatStops, topographicPlacesCounter);


            synchronized (STOP_PLACE_IMPORT_LOCK) {
                return siteFrameImporter.importStopPlaces(tiamatStops, stopPlaceImporter, stopPlacesCreated);
            }

//            List<PathLink> pathLinks = netexMapper,


           // TODO originalIds+"-response
            // TODO site frame version
            return publicationDeliveryExporter.exportSiteFrame(siteFrameWithProcessedStopPlaces);
        } finally {
            MDC.remove(IMPORT_CORRELATION_ID);
            loggerTimer.cancel();
        }
    }

    private int numberOfStops(SiteFrame netexSiteFrame) {
        return netexSiteFrame.getStopPlaces().getStopPlace().size();
    }

    private SiteFrame findSiteFrame(PublicationDeliveryStructure incomingPublicationDelivery) {
        return incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame()
                .stream()
                .filter(element -> element.getValue() instanceof SiteFrame)
                .map(element -> (SiteFrame) element.getValue())
                .findFirst().get();
    }
}
