package org.rutebanken.tiamat.importer;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.importer.modifier.CompassBearingRemover;
import org.rutebanken.tiamat.importer.modifier.name.*;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Service
public class PublicationDeliveryImporter {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryImporter.class);

    public static final String IMPORT_CORRELATION_ID = "importCorrelationId";
    private static final Object IMPORT_LOCK = new Object();

    private final CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;
    private final StopPlaceImporter stopPlaceImporter;
    private final SiteFrameImporter siteFrameImporter;
    private final PublicationDeliveryExporter publicationDeliveryExporter;
    private final NetexMapper netexMapper;
    private final StopPlaceNameCleaner stopPlaceNameCleaner;
    private final NameToDescriptionMover nameToDescriptionMover;
    private final QuayNameRemover quayNameRemover;
    private final StopPlaceNameNumberToQuayMover stopPlaceNameNumberToQuayMover;
    private final QuayDescriptionPlatformCodeExtractor quayDescriptionPlatformCodeExtractor;
    private final CompassBearingRemover compassBearingRemover;

    @Autowired
    public PublicationDeliveryImporter(NetexMapper netexMapper,
                                       CountyAndMunicipalityLookupService countyAndMunicipalityLookupService,
                                       @Qualifier("mergingStopPlaceImporter") StopPlaceImporter stopPlaceImporter,
                                       SiteFrameImporter siteFrameImporter, PublicationDeliveryExporter publicationDeliveryExporter, StopPlaceNameCleaner stopPlaceNameCleaner, NameToDescriptionMover nameToDescriptionMover, QuayNameRemover quayNameRemover, StopPlaceNameNumberToQuayMover stopPlaceNameNumberToQuayMover, QuayDescriptionPlatformCodeExtractor quayDescriptionPlatformCodeExtractor, CompassBearingRemover compassBearingRemover) {
        this.netexMapper = netexMapper;
        this.countyAndMunicipalityLookupService = countyAndMunicipalityLookupService;
        this.stopPlaceImporter = stopPlaceImporter;
        this.siteFrameImporter = siteFrameImporter;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.stopPlaceNameCleaner = stopPlaceNameCleaner;
        this.nameToDescriptionMover = nameToDescriptionMover;
        this.quayNameRemover = quayNameRemover;
        this.stopPlaceNameNumberToQuayMover = stopPlaceNameNumberToQuayMover;
        this.quayDescriptionPlatformCodeExtractor = quayDescriptionPlatformCodeExtractor;
        this.compassBearingRemover = compassBearingRemover;
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
                    .map(tiamatSiteFrame -> {
                        List<StopPlace> stops = tiamatSiteFrame.getStopPlaces().getStopPlace().parallelStream()
                                .peek(stopPlace -> MDC.put(PublicationDeliveryImporter.IMPORT_CORRELATION_ID, tiamatSiteFrame.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).toString()))
                                .map(stopPlace -> compassBearingRemover.remove(stopPlace))
                                .map(stopPlace -> stopPlaceNameCleaner.cleanNames(stopPlace))
                                .map(stopPlace -> nameToDescriptionMover.updateDescriptionFromName(stopPlace))
                                .map(stopPlace -> quayNameRemover.removeQuayNameIfEqualToStopPlaceName(stopPlace))
                                .map(stopPlace -> stopPlaceNameNumberToQuayMover.moveNumberEndingToQuay(stopPlace))
                                .map(stopPlace -> quayDescriptionPlatformCodeExtractor.extractPlatformCodes(stopPlace))
                                .map(stopPlace -> {
                                    try {
                                        countyAndMunicipalityLookupService.populateCountyAndMunicipality(stopPlace, topographicPlacesCounter);
                                    } catch (IOException |InterruptedException e) {
                                        logger.warn("Error looking up county and municipality", e);
                                    }
                                    return stopPlace;
                                }).collect(toList());
                        tiamatSiteFrame.getStopPlaces().getStopPlace().clear();
                        tiamatSiteFrame.getStopPlaces().getStopPlace().addAll(stops);
                        return tiamatSiteFrame;
                    })
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
