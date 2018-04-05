package org.rutebanken.tiamat.service.batch;

import com.google.common.collect.Lists;
import javafx.scene.paint.Stop;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.hibernate.validator.HibernateValidator;
import org.rutebanken.tiamat.exporter.async.ParentStopFetchingIterator;
import org.rutebanken.tiamat.exporter.eviction.SessionEntitiesEvictor;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.rutebanken.tiamat.lock.TimeoutMaxLeaseTimeLock;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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
    public static final int FLUSH_EACH = 100;
    public static final int MAX_LEASE_TIME_SECONDS = 7200;
    public static final int WAIT_TIMEOUT_SECONDS = 1000;
    public static final String BACKGROUND_UPDATE_STOPS_LOCK = "background-update-stops-lock";

    private final StopPlaceRepository stopPlaceRepository;

    private final TariffZonesLookupService tariffZonesLookupService;
    private final TopographicPlaceLookupService topographicPlaceLookupService;
    private final EntityManager entityManager;
    private final TimeoutMaxLeaseTimeLock timeoutMaxLeaseTimeLock;

    @Autowired
    public StopPlaceRefUpdaterService(StopPlaceRepository stopPlaceRepository,
                                      TariffZonesLookupService tariffZonesLookupService,
                                      TopographicPlaceLookupService topographicPlaceLookupService,
                                      EntityManager entityManager,
                                      TimeoutMaxLeaseTimeLock timeoutMaxLeaseTimeLock) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.tariffZonesLookupService = tariffZonesLookupService;
        this.topographicPlaceLookupService = topographicPlaceLookupService;
        this.entityManager = entityManager;
        this.timeoutMaxLeaseTimeLock = timeoutMaxLeaseTimeLock;
    }

    public void updateAllStopPlaces() {

        // To avoid multiple hazelcast instances doing the same job
        timeoutMaxLeaseTimeLock.executeInLock(() -> {

            try {
                updateStops();
            } catch (Exception e){
                logger.error("Error updating stops", e);
            }

            return null;
        }, BACKGROUND_UPDATE_STOPS_LOCK, WAIT_TIMEOUT_SECONDS, MAX_LEASE_TIME_SECONDS);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStops() {

        long startTime = System.currentTimeMillis();

        Session session = entityManager.unwrap(Session.class);
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
        AtomicInteger stopsSavedTotal = new AtomicInteger();
        AtomicInteger stopsCounter = new AtomicInteger();
        PerSecondLogger perSecondsLogger = new PerSecondLogger(startTime, stopsSavedTotal, "Progress while updating stop places references");

        while (stopPlaceIterator.hasNext()) {
            try {
                stopsCounter.incrementAndGet();

                StopPlace existingStopPlace = stopPlaceIterator.next();

                Optional<StopPlace> optionalStopPlace = new StopPlaceRefUpdater(
                        tariffZonesLookupService,
                        topographicPlaceLookupService,
                        existingStopPlace,
                        updatedBecauseOfTariffZoneRefChange,
                        updatedBecauseOfTopographicPlaceChange)
                        .call();

                if (optionalStopPlace.isPresent()) {
                    stopsSavedTotal.incrementAndGet();
                    StopPlace stopPlaceToSave = optionalStopPlace.get();
                    stopPlaceToSave.setChanged(Instant.now());
                    entityManager.detach(stopPlaceToSave);
                    stopPlaceRepository.saveAndFlush(stopPlaceToSave);
                    logger.trace("Saved stop {}", stopPlaceToSave);
                    perSecondsLogger.log();
                    session.flush();
                    if (stopsCounter.get() % FLUSH_EACH == 0 && !stopPlaceIterator.hasNextParent()) {
                        logger.trace("Flushing and clearing session at count {}", stopsCounter.get());
                        session.clear();
                    } else {
                        sessionEntitiesEvictor.evictKnownEntitiesFromSession(stopPlaceToSave);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        long timeSpent = System.currentTimeMillis() - startTime;
        logger.info("Updated {} stops in {} ms. Stops updated because of change in tariff zone refs: {}, " +
                        "stops updated because of change in topographic place ref: {}",
                stopsSavedTotal, timeSpent, updatedBecauseOfTariffZoneRefChange, updatedBecauseOfTopographicPlaceChange);
    }

}
