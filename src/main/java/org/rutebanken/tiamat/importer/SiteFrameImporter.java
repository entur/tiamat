package org.rutebanken.tiamat.importer;

import com.google.common.util.concurrent.Striped;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.importer.modifier.CompassBearingRemover;
import org.rutebanken.tiamat.importer.modifier.name.*;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

/**
 * When importing site frames with the matching stops concurrently, not thread safe.
 */
@Component
@Transactional
public class SiteFrameImporter {

    private static final Logger logger = LoggerFactory.getLogger(SiteFrameImporter.class);

    private final TopographicPlaceCreator topographicPlaceCreator;

    @Autowired
    public SiteFrameImporter(TopographicPlaceCreator topographicPlaceCreator) {
        this.topographicPlaceCreator = topographicPlaceCreator;

    }

    public org.rutebanken.netex.model.SiteFrame importSiteFrame(SiteFrame siteFrame, StopPlaceImporter stopPlaceImporter) {
        long startTime = System.currentTimeMillis();
        AtomicInteger stopPlacesCreated = new AtomicInteger(0);
        AtomicInteger topographicPlacesCreated = new AtomicInteger(0);

        logger.info("Received site frame for import: {}", siteFrame);

        final String originalIds = siteFrame.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).toString();

        Timer timer = new Timer(this.getClass().getName()+"-logger");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                logStatus(stopPlacesCreated, startTime, siteFrame, topographicPlacesCreated, originalIds);
            }
        };
        timer.scheduleAtFixedRate(timerTask, 2000, 2000);

        try {
            org.rutebanken.netex.model.SiteFrame netexSiteFrame = new org.rutebanken.netex.model.SiteFrame();
            netexSiteFrame
                    .withId(originalIds+"-response")
                    .withVersion("1");
            if(siteFrame.getStopPlaces() != null) {
                List<org.rutebanken.netex.model.StopPlace> createdStopPlaces = siteFrame.getStopPlaces().getStopPlace()
                        .stream()
                        .peek(stopPlace -> MDC.put(PublicationDeliveryImporter.IMPORT_CORRELATION_ID, originalIds))
                        .map(stopPlace ->
                                importStopPlace(stopPlaceImporter, stopPlace, siteFrame, topographicPlacesCreated, stopPlacesCreated)
                        )
                        .filter(Objects::nonNull)
                        .collect(toList());

                logger.info("Saved {} topographical places and {} stop places", topographicPlacesCreated, stopPlacesCreated);



                topographicPlaceCreator.invalidateCache();

                netexSiteFrame.withStopPlaces(
                            new StopPlacesInFrame_RelStructure()
                                    .withStopPlace(distinctByIdAndHighestVersion(createdStopPlaces)));
            } else {
                logger.info("Site frame does not contain any stop places: {}", siteFrame);
            }
            return netexSiteFrame;
        } finally {
            timer.cancel();
        }
    }

    private org.rutebanken.netex.model.StopPlace importStopPlace(StopPlaceImporter stopPlaceImporter, StopPlace stopPlace, SiteFrame siteFrame, AtomicInteger topographicPlacesCreated, AtomicInteger stopPlacesCreated) {

        try {
            org.rutebanken.netex.model.StopPlace importedStop = stopPlaceImporter.importStopPlace(stopPlace, siteFrame, topographicPlacesCreated);
            stopPlacesCreated.incrementAndGet();
            return importedStop;

        } catch (Exception e) {
            throw new RuntimeException("Could not import stop place "+stopPlace, e);
        }
    }

    /**
     * In order to get a distinct list over stop places, and the newest version if duplicates.
     * @param stopPlaces
     * @return unique list with stop places based on ID
     */
    public Collection<org.rutebanken.netex.model.StopPlace> distinctByIdAndHighestVersion(List<org.rutebanken.netex.model.StopPlace> stopPlaces) {
        Map<String, org.rutebanken.netex.model.StopPlace> uniqueStopPlaces = new HashMap<>();
        for(org.rutebanken.netex.model.StopPlace stopPlace : stopPlaces) {
            if(uniqueStopPlaces.containsKey(stopPlace.getId())) {
                org.rutebanken.netex.model.StopPlace existingStopPlace = uniqueStopPlaces.get(stopPlace.getId());
                long existingStopVersion = tryParseLong(existingStopPlace.getVersion());
                long stopPlaceVersion = tryParseLong(stopPlace.getVersion());
                if(existingStopVersion < stopPlaceVersion) {
                    logger.info("Returning newest version of stop place with ID {}: {}", stopPlace.getId(), stopPlace.getVersion());
                    uniqueStopPlaces.put(stopPlace.getId(), stopPlace);
                }
            } else {
                uniqueStopPlaces.put(stopPlace.getId(), stopPlace);
            }
        }
        return uniqueStopPlaces.values();
    }

    private long tryParseLong(String version) {
        try {
            return Long.parseLong(version);
        } catch(NumberFormatException|NullPointerException e) {
            return 0L;
        }
    }

    private void logStatus(AtomicInteger stopPlacesCreated, long startTime, SiteFrame siteFrame, AtomicInteger topographicPlacesCreated, String originalIds) {
        long duration = System.currentTimeMillis() - startTime;

        MDC.put(PublicationDeliveryImporter.IMPORT_CORRELATION_ID, originalIds);
        String stopPlacesPerSecond = "NA";

        if(duration >= 1000) {

            stopPlacesPerSecond = String.valueOf(stopPlacesCreated.get() / (duration / 1000f));
        }
        int total = siteFrame.getStopPlaces().getStopPlace().size();
        logger.info("Stop place {}/{} - {}% - {} spl/sec - {} topographic places", stopPlacesCreated.get(),
                siteFrame.getStopPlaces().getStopPlace().size(),
                (stopPlacesCreated.get() * 100f) / total,
                stopPlacesPerSecond,
                topographicPlacesCreated);

    }
}
