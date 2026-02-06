package org.rutebanken.tiamat.service.batch;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.rutebanken.tiamat.exporter.async.ParentStopFetchingIterator;
import org.rutebanken.tiamat.exporter.eviction.SessionEntitiesEvictor;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.rutebanken.tiamat.lock.LockException;
import org.rutebanken.tiamat.lock.TimeoutMaxLeaseTimeLock;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for updating references from stop places to tariff zone and topographic place based on polygons
 */
@Service
@Transactional
public class StopPlaceRefUpdaterService {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceRefUpdaterService.class);
    public static final int CLEAR_EACH = 100;
    public static final int MAX_LEASE_TIME_SECONDS = 7200;
    public static final int WAIT_TIMEOUT_SECONDS = 10;
    public static final String BACKGROUND_UPDATE_STOPS_LOCK = "background-update-stops-lock";

    private final StopPlaceRepository stopPlaceRepository;
    private final TariffZoneRepository tariffZoneRepository;
    private final FareZoneRepository fareZoneRepository;
    private final TopographicPlaceRepository topographicPlaceRepository;

    private final TariffZonesLookupService tariffZonesLookupService;
    private final TopographicPlaceLookupService topographicPlaceLookupService;
    private final EntityManager entityManager;
    private final TimeoutMaxLeaseTimeLock timeoutMaxLeaseTimeLock;

    private final boolean enableLegacyUpdater;

    @Autowired
    public StopPlaceRefUpdaterService(StopPlaceRepository stopPlaceRepository,
                                      TariffZoneRepository tariffZoneRepository,
                                      FareZoneRepository fareZoneRepository,
                                      TopographicPlaceRepository topographicPlaceRepository,
                                      TariffZonesLookupService tariffZonesLookupService,
                                      TopographicPlaceLookupService topographicPlaceLookupService,
                                      EntityManager entityManager,
                                      TimeoutMaxLeaseTimeLock timeoutMaxLeaseTimeLock,
                                      @Value("${stopPlaceRefUpdaterService.enableLegacyUpdater:false}") boolean enableLegacyUpdater) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.tariffZoneRepository = tariffZoneRepository;
        this.fareZoneRepository = fareZoneRepository;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.tariffZonesLookupService = tariffZonesLookupService;
        this.topographicPlaceLookupService = topographicPlaceLookupService;
        this.entityManager = entityManager;
        this.timeoutMaxLeaseTimeLock = timeoutMaxLeaseTimeLock;
        this.enableLegacyUpdater = enableLegacyUpdater;
    }

    public void updateAllStopPlaces() {

        try {
            // To avoid multiple hazelcast instances doing the same job
            timeoutMaxLeaseTimeLock.executeInLock(() -> {

                try {
                    if (enableLegacyUpdater) {
                        updateStopsLegacy();
                    } else {
                        updateStops();
                    }
                } catch (Exception e) {
                    logger.error("Error updating stops", e);
                }

                return null;
            }, BACKGROUND_UPDATE_STOPS_LOCK, WAIT_TIMEOUT_SECONDS, MAX_LEASE_TIME_SECONDS);
        } catch (LockException lockException) {
            logger.info(lockException.getMessage());
        } catch (RuntimeException e) {
            logger.warn("Background job stopped because of exception", e);
        }
    }

    /**
     * Updates stop place refs (tariff zones, fare zones, and topographic places) using native queries.
     * This is significantly faster than the legacy updater which processes stops one-by-one.
     */
    public void updateStops() {
        logger.info("About to update all currently valid stop places (tariff zone, fare zone, and topographic place refs)");
        long totalStartTime = System.currentTimeMillis();

        long stepStartTime = System.currentTimeMillis();
        stopPlaceRepository.deleteStopPlaceTariffZoneRefs();
        logger.info("Deleted stop place tariff zone refs in {} ms", System.currentTimeMillis() - stepStartTime);

        stepStartTime = System.currentTimeMillis();
        int tariffZoneUpdates = tariffZoneRepository.updateStopPlaceTariffZoneRef();
        logger.info("Updated {} tariff zone refs in {} ms", tariffZoneUpdates, System.currentTimeMillis() - stepStartTime);

        stepStartTime = System.currentTimeMillis();
        fareZoneRepository.updateStopPlaceTariffZoneRef();
        logger.info("Updated fare zone refs in {} ms", System.currentTimeMillis() - stepStartTime);

        stepStartTime = System.currentTimeMillis();
        int topographicPlaceUpdates = topographicPlaceRepository.updateStopPlaceTopographicPlaceRef();
        logger.info("Updated {} topographic place refs in {} ms", topographicPlaceUpdates, System.currentTimeMillis() - stepStartTime);

        long totalTimeSpent = System.currentTimeMillis() - totalStartTime;
        logger.info("Completed all stop place ref updates in {} ms", totalTimeSpent);
    }

    /*
     * Legacy updater stop-place ref  update stops one by one
     */

    public void updateStopsLegacy() {

        long startTime = System.currentTimeMillis();

        Session session = entityManager.unwrap(SessionImpl.class);
        logger.info("About to update all currently valid stop places (tariff zone and topographic place refs)");

        SessionEntitiesEvictor sessionEntitiesEvictor = new SessionEntitiesEvictor((SessionImpl) session);

        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.CURRENT)
                                .build())
                .build();
        logger.info("Created export params search for scrolling stop places {}", exportParams);

        ParentStopFetchingIterator stopPlaceIterator = new ParentStopFetchingIterator(stopPlaceRepository.scrollStopPlaces(exportParams), stopPlaceRepository);

        AtomicInteger updatedBecauseOfTariffZoneRefChange = new AtomicInteger();
        AtomicInteger updatedBecauseOfTopographicPlaceChange = new AtomicInteger();
        AtomicInteger stopsSaved = new AtomicInteger();
        AtomicInteger stopsIterated = new AtomicInteger();
        PerSecondLogger perSecondsLogger = new PerSecondLogger(startTime, stopsIterated, stopsSaved, "Progress while updating stop places references");

        while (stopPlaceIterator.hasNext()) {
            try {
                stopsIterated.incrementAndGet();

                StopPlace existingStopPlace = stopPlaceIterator.next();

                Optional<StopPlace> optionalStopPlace = new StopPlaceRefUpdater(
                        tariffZonesLookupService,
                        topographicPlaceLookupService,
                        existingStopPlace,
                        updatedBecauseOfTariffZoneRefChange,
                        updatedBecauseOfTopographicPlaceChange)
                        .call();

                if (optionalStopPlace.isPresent()) {
                    stopsSaved.incrementAndGet();
                    StopPlace stopPlaceToSave = optionalStopPlace.get();
                        stopPlaceToSave.setChanged(Instant.now());

                        // Issues with topographic place not being updated.
                        // https://stackoverflow.com/a/2370276
                        // https://stackoverflow.com/a/5709244

                        if (session.contains(stopPlaceToSave)) {
                            session.evict(stopPlaceToSave);
                        }

                        session.update(stopPlaceToSave);


                        logger.trace("Saved stop {}", stopPlaceToSave);
                        session.flush();
                        if (stopsIterated.get() % CLEAR_EACH == 0 && !stopPlaceIterator.hasNextParent()) {
                            logger.trace("Flushing and clearing session at count {}", stopsIterated.get());
                            session.clear();
                        } else {
                            sessionEntitiesEvictor.evictKnownEntitiesFromSession(stopPlaceToSave);
                        }
                } else if (!stopPlaceIterator.hasNextParent()) {
                    session.flush();
                    session.clear();
                }
                perSecondsLogger.log();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        long timeSpent = System.currentTimeMillis() - startTime;
        logger.info("Updated {} stops in {} ms. Stops updated because of change in tariff zone refs: {}, " +
                        "stops updated because of change in topographic place ref: {}",
                stopsSaved, timeSpent, updatedBecauseOfTariffZoneRefChange, updatedBecauseOfTopographicPlaceChange);
    }

}
