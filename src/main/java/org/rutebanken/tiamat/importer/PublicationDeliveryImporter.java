package org.rutebanken.tiamat.importer;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.importer.modifier.StopPlacePreModificator;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PublicationDeliveryImporter {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryImporter.class);

    public static final String IMPORT_CORRELATION_ID = "importCorrelationId";
    private static final Object IMPORT_LOCK = new Object();

    private final StopPlaceImporter stopPlaceImporter;
    private final SiteFrameImporter siteFrameImporter;
    private final PublicationDeliveryExporter publicationDeliveryExporter;
    private final NetexMapper netexMapper;
    private final StopPlacePreModificator stopPlacePreModificator;


    @Autowired
    public PublicationDeliveryImporter(NetexMapper netexMapper,
                                       @Qualifier("mergingStopPlaceImporter") StopPlaceImporter stopPlaceImporter,
                                       SiteFrameImporter siteFrameImporter, PublicationDeliveryExporter publicationDeliveryExporter, StopPlacePreModificator stopPlacePreModificator) {
        this.netexMapper = netexMapper;

        this.stopPlaceImporter = stopPlaceImporter;
        this.siteFrameImporter = siteFrameImporter;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.stopPlacePreModificator = stopPlacePreModificator;
    }


    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure importPublicationDelivery(PublicationDeliveryStructure incomingPublicationDelivery) {
        if(incomingPublicationDelivery.getDataObjects() == null) {
            String responseMessage = "Received publication delivery but it does not contain any data objects.";
            logger.warn(responseMessage);
            throw new RuntimeException(responseMessage);
        }
        logger.info("Got publication delivery with {} site frames", incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame().size());

        AtomicInteger topographicPlacesCounter = new AtomicInteger();

        try {
            org.rutebanken.netex.model.SiteFrame siteFrameWithProcessedStopPlaces = incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame()
                    .stream()
                    .filter(element -> element.getValue() instanceof SiteFrame)
                    .map(element -> (SiteFrame) element.getValue())
                    .peek(netexSiteFrame -> {
                        MDC.put(IMPORT_CORRELATION_ID, netexSiteFrame.getId());
                        logger.info("Publication delivery contains site frame created at {}", netexSiteFrame.getCreated());
                    })
                    .map(netexSiteFrame -> netexMapper.mapToTiamatModel(netexSiteFrame))
                    .map(tiamatSiteFrame -> stopPlacePreModificator.modify(tiamatSiteFrame, topographicPlacesCounter))
                    .map(tiamatSiteFrame -> {
                        synchronized (IMPORT_LOCK) {
                            return siteFrameImporter.importSiteFrame(tiamatSiteFrame, stopPlaceImporter);
                        }
                    })
                    .findFirst().get();

            return publicationDeliveryExporter.exportSiteFrame(siteFrameWithProcessedStopPlaces);
        } finally {
            MDC.remove(IMPORT_CORRELATION_ID);
        }
    }
}
