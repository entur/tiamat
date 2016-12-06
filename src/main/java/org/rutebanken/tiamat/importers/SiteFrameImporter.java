package org.rutebanken.tiamat.importers;

import com.google.common.util.concurrent.Striped;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Component
public class SiteFrameImporter {

    private static final Logger logger = LoggerFactory.getLogger(SiteFrameImporter.class);

    private static final Striped<Semaphore> stripedSemaphores = Striped.lazyWeakSemaphore(Integer.MAX_VALUE, 1);

    private final TopographicPlaceCreator topographicPlaceCreator;
    private final NetexMapper netexMapper;
    private final StopPlaceNameCleaner stopPlaceNameCleaner;
    private final NameToDescriptionMover nameToDescriptionMover;
    private final QuayNameRemover quayNameRemover;

    @Autowired
    public SiteFrameImporter(TopographicPlaceCreator topographicPlaceCreator, NetexMapper netexMapper, StopPlaceNameCleaner stopPlaceNameCleaner, NameToDescriptionMover nameToDescriptionMover, QuayNameRemover quayNameRemover) {
        this.topographicPlaceCreator = topographicPlaceCreator;
        this.netexMapper = netexMapper;
        this.stopPlaceNameCleaner = stopPlaceNameCleaner;
        this.nameToDescriptionMover = nameToDescriptionMover;
        this.quayNameRemover = quayNameRemover;
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
                        .parallelStream()
                        .peek(stopPlace -> MDC.put(PublicationDeliveryResource.IMPORT_CORRELATION_ID, originalIds))
                        .map(stopPlace -> stopPlaceNameCleaner.cleanNames(stopPlace))
                        .map(stopPlace -> nameToDescriptionMover.updateDescriptionFromName(stopPlace))
                        .map(stopPlace -> quayNameRemover.removeQuayNameIfEqualToStopPlaceName(stopPlace))
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
                logger.info("Site frame does not contain any stop places: ", siteFrame);
            }
            return netexSiteFrame;
        } finally {
            timerTask.cancel();
        }
    }

    /**
     * When importing site frames in multiple threads, and those site frames might contain different stop place that will be merged,
     * we run into the risc of having multiple threads trying to save the same stop place.
     *
     * That's why we use a striped semaphore to not work on the same stop place concurrently.
     * it is important to flush the session between each stop place, *before* the semaphore has been released.
     *
     * Attempts to use saveAndFlush or hibernate flush mode always have not been successful.
     */
    @Transactional
    private org.rutebanken.netex.model.StopPlace importStopPlaceInsideLock(StopPlaceImporter stopPlaceImporter, StopPlace stopPlace, SiteFrame siteFrame, AtomicInteger topographicPlacesCreated, AtomicInteger stopPlacesCreated) throws ExecutionException, InterruptedException {
        StopPlace importedStopPlace = stopPlaceImporter.importStopPlace(stopPlace, siteFrame, topographicPlacesCreated);
        stopPlacesCreated.incrementAndGet();
        return netexMapper.mapToNetexModel(importedStopPlace);
    }

    private org.rutebanken.netex.model.StopPlace importStopPlace(StopPlaceImporter stopPlaceImporter, StopPlace stopPlace, SiteFrame siteFrame, AtomicInteger topographicPlacesCreated, AtomicInteger stopPlacesCreated) {
        String semaphoreKey = getStripedSemaphoreKey(stopPlace);
        Semaphore semaphore = stripedSemaphores.get(semaphoreKey);

        try {
            semaphore.acquire();
            logger.info("Aquired semaphore '{}' for stop place {}", semaphoreKey, stopPlace);
            return importStopPlaceInsideLock(stopPlaceImporter, stopPlace, siteFrame, topographicPlacesCreated, stopPlacesCreated);
        } catch (Exception e) {
            // When having issues with one stop place, do not fail for all other stop places in publication delivery.
            logger.error("Caught exception while importing stop place. Semaphore was " + semaphoreKey, e);
            return null;
        } finally {
            semaphore.release();
            logger.info("Released semaphore '{}'", semaphoreKey);
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

        MDC.put(PublicationDeliveryResource.IMPORT_CORRELATION_ID, originalIds);
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


    private String getStripedSemaphoreKey(StopPlace stopPlace) {
        final String semaphoreKey;
        if (stopPlace.getName() != null
                && stopPlace.getName().getValue() != null
                && !stopPlace.getName().getValue().isEmpty()) {
            semaphoreKey = "name-" + stopPlace.getName().getValue();
        } else {
            semaphoreKey = "all";
        }
        return semaphoreKey;
    }
}
