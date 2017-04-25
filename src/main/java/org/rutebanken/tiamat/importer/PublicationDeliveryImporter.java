package org.rutebanken.tiamat.importer;

import org.apache.commons.lang.NotImplementedException;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.Common_VersionFrameStructure;
import org.rutebanken.netex.model.CompositeFrame;
import org.rutebanken.netex.model.PathLinksInFrame_RelStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.exporter.TopographicPlacesExporter;
import org.rutebanken.tiamat.importer.filter.ZoneCountyFilterer;
import org.rutebanken.tiamat.importer.initial.ParallelInitialStopPlaceImporter;
import org.rutebanken.tiamat.importer.log.ImportLogger;
import org.rutebanken.tiamat.importer.log.ImportLoggerTask;
import org.rutebanken.tiamat.importer.modifier.StopPlacePreSteps;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PublicationDeliveryImporter {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryImporter.class);

    public static final String IMPORT_CORRELATION_ID = "importCorrelationId";
    private static final Object STOP_PLACE_IMPORT_LOCK = new Object();

    /**
     * Make this configurable. Export topographic places in response.
     */
    private static final boolean EXPORT_TOPOGRAPHIC_PLACES_FOR_STOPS = false;

    private final TransactionalStopPlacesImporter transactionalStopPlacesImporter;
    private final PublicationDeliveryExporter publicationDeliveryExporter;
    private final NetexMapper netexMapper;
    private final StopPlacePreSteps stopPlacePreSteps;
    private final PathLinksImporter pathLinksImporter;
    private final TopographicPlacesExporter topographicPlacesExporter;
    private final TopographicPlaceImporter topographicPlaceImporter;
    private final ZoneCountyFilterer zoneCountyFilterer;
    private final ParallelInitialStopPlaceImporter parallelInitialStopPlaceImporter;
    private final MatchingIdAppendingStopPlacesImporter matchingIdAppendingStopPlacesImporter;



    @Autowired
    public PublicationDeliveryImporter(NetexMapper netexMapper,
                                       TransactionalStopPlacesImporter transactionalStopPlacesImporter,
                                       PublicationDeliveryExporter publicationDeliveryExporter,
                                       StopPlacePreSteps stopPlacePreSteps,
                                       PathLinksImporter pathLinksImporter,
                                       TopographicPlacesExporter topographicPlacesExporter, TopographicPlaceImporter topographicPlaceImporter, ZoneCountyFilterer zoneCountyFilterer, ParallelInitialStopPlaceImporter parallelInitialStopPlaceImporter, MatchingIdAppendingStopPlacesImporter matchingIdAppendingStopPlacesImporter) {
        this.netexMapper = netexMapper;
        this.transactionalStopPlacesImporter = transactionalStopPlacesImporter;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.stopPlacePreSteps = stopPlacePreSteps;
        this.pathLinksImporter = pathLinksImporter;
        this.topographicPlacesExporter = topographicPlacesExporter;
        this.topographicPlaceImporter = topographicPlaceImporter;
        this.zoneCountyFilterer = zoneCountyFilterer;
        this.parallelInitialStopPlaceImporter = parallelInitialStopPlaceImporter;
        this.matchingIdAppendingStopPlacesImporter = matchingIdAppendingStopPlacesImporter;
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


            if(hasStops(netexSiteFrame)) {
                List<org.rutebanken.tiamat.model.StopPlace> tiamatStops = netexMapper.mapStopsToTiamatModel(netexSiteFrame.getStopPlaces().getStopPlace());
                tiamatStops = stopPlacePreSteps.run(tiamatStops, topographicPlacesCounter);

                int numberOfStopBeforeFiltering = tiamatStops.size();
                logger.info("About to filter {} stops based on county references: {}", tiamatStops.size(), publicationDeliveryParams.onlyImportStopsInCounties);
                tiamatStops = (List<org.rutebanken.tiamat.model.StopPlace>) zoneCountyFilterer.filterByCountyMatch(publicationDeliveryParams.onlyImportStopsInCounties, tiamatStops);
                logger.info("Got {} stops (was {}) after filtering by: {}", tiamatStops.size(), numberOfStopBeforeFiltering, publicationDeliveryParams.onlyImportStopsInCounties);

                final Collection<org.rutebanken.netex.model.StopPlace> importedNetexStopPlaces;
                logger.info("The import type is: {}", publicationDeliveryParams.importType);

                if(publicationDeliveryParams.importType == null || publicationDeliveryParams.importType.equals(ImportType.MERGE)) {
                    synchronized (STOP_PLACE_IMPORT_LOCK) {
                        importedNetexStopPlaces = transactionalStopPlacesImporter.importStopPlaces(tiamatStops, stopPlacesCreatedOrUpdated);
                    }
                } else if(publicationDeliveryParams.importType.equals(ImportType.INITIAL)) {
                    importedNetexStopPlaces = parallelInitialStopPlaceImporter.importStopPlaces(tiamatStops, stopPlacesCreatedOrUpdated);
                } else if(publicationDeliveryParams.importType.equals(ImportType.MATCH)) {
                    if(publicationDeliveryParams.onlyMatchAndAppendStopsOutsideCounties != null && !publicationDeliveryParams.onlyMatchAndAppendStopsOutsideCounties.isEmpty()) {
                        logger.info("Only matching and appending original id for stops that is outside given list of counties: {}", publicationDeliveryParams.onlyMatchAndAppendStopsOutsideCounties);
                        tiamatStops = (List<org.rutebanken.tiamat.model.StopPlace>) zoneCountyFilterer.filterByCountyMatch(publicationDeliveryParams.onlyMatchAndAppendStopsOutsideCounties, tiamatStops, true);
                        logger.info("Got {} stops back from zone filter", tiamatStops.size());
                    }
                    logger.info("Importing {} stops", tiamatStops.size());
                    importedNetexStopPlaces = matchingIdAppendingStopPlacesImporter.importStopPlaces(tiamatStops, stopPlacesCreatedOrUpdated);
                } else {
                    throw new NotImplementedException("Import type " + publicationDeliveryParams.importType + " not implemented ");
                }
                logger.info("Imported/matched/updated {} stop places", stopPlacesCreatedOrUpdated);


                if (EXPORT_TOPOGRAPHIC_PLACES_FOR_STOPS) {
                    List<TopographicPlace> netexTopographicPlaces = topographicPlacesExporter.export(findTopographicPlaceRefsFromStops(tiamatStops));

                    if (!netexTopographicPlaces.isEmpty()) {
                        responseSiteframe.withTopographicPlaces(
                                new TopographicPlacesInFrame_RelStructure()
                                        .withTopographicPlace(netexTopographicPlaces));
                    }
                } else {
                    clearTopographicPlaceRefs(importedNetexStopPlaces);
                }

                if (!importedNetexStopPlaces.isEmpty()) {
                    responseSiteframe.withStopPlaces(
                            new StopPlacesInFrame_RelStructure()
                                    .withStopPlace(importedNetexStopPlaces));
                } else {
                    logger.info("No stops in response");
                }
            }

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

    private void validate(PublicationDeliveryParams publicationDeliveryParams) {
        if(publicationDeliveryParams.onlyImportStopsInCounties != null && publicationDeliveryParams.onlyMatchAndAppendStopsOutsideCounties != null) {
            if(!publicationDeliveryParams.onlyImportStopsInCounties.isEmpty() && ! publicationDeliveryParams.onlyMatchAndAppendStopsOutsideCounties.isEmpty()) {
                throw new IllegalArgumentException("onlyImportStopsInCounties and onlyImportStopsInCounties cannot be specified at the same time!");
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
