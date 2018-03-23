package org.rutebanken.tiamat.service.batch;

import org.rutebanken.tiamat.exporter.async.ParentStopFetchingIterator;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for updating references from stop places to tariff zone and topographic place based on polygons
 */
@Transactional
@Service
public class StopPlaceUpdaterService {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceUpdaterService.class);

    private final StopPlaceRepository stopPlaceRepository;
    private final TariffZonesLookupService tariffZonesLookupService;
    private final TopographicPlaceLookupService topographicPlaceLookupService;
//    private final BlockingQueue<Runnable> stopsToUpdateQueue = new ArrayBlockingQueue<>(100);
    private final int threads;

    @Autowired
    public StopPlaceUpdaterService(StopPlaceRepository stopPlaceRepository, TariffZonesLookupService tariffZonesLookupService, TopographicPlaceLookupService topographicPlaceLookupService) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.tariffZonesLookupService = tariffZonesLookupService;
        this.topographicPlaceLookupService = topographicPlaceLookupService;


        int availableProcessors = Runtime.getRuntime().availableProcessors();
        threads = Math.abs(availableProcessors / 2);
        logger.info("Will start threadpool with {} threads", threads);

//        executorService = new ThreadPoolExecutor(threads, threads,
//                5000L, TimeUnit.MILLISECONDS,
//                stopsToUpdateQueue, (runnable) -> new Thread(runnable, "stop-updater"));

    }

    public void updateAllStopPlaces() {
        long startTime = System.currentTimeMillis();

        logger.info("About to update all currently valid stop places (tariff zone and topographic place refs");

        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                                .build())
                .build();
        logger.info("Created export params search for scrolling stop places {}", exportParams);

        Iterator<StopPlace> stopPlaceIterator = new ParentStopFetchingIterator(stopPlaceRepository.scrollStopPlaces(exportParams), stopPlaceRepository);

        AtomicInteger updatedBecauseOfTariffZoneRefChange = new AtomicInteger();
        AtomicInteger updatedBecauseOfTopographicPlaceChange = new AtomicInteger();
        AtomicInteger stopsSavedTotal = new AtomicInteger();

        while (stopPlaceIterator.hasNext()) {
            try {
                Optional<StopPlace> optionalStopPlace = new StopPlaceRefUpdater(
                         tariffZonesLookupService,
                         topographicPlaceLookupService,
                         stopPlaceIterator.next(),
                         updatedBecauseOfTariffZoneRefChange,
                         updatedBecauseOfTopographicPlaceChange).call();
                if(optionalStopPlace.isPresent()) {
                    stopsSavedTotal.incrementAndGet();
                    StopPlace stopPlace = optionalStopPlace.get();
                    stopPlaceRepository.save(stopPlace);
                    logger.info("Saved stop {}", stopPlace);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();

        logger.info("Updating stops finished in {} ms. Stops updated because of change in tariff zone refs: {}, " +
                "stops updated because of change in topographic place ref: {}, " +
                        "stops updated in total: {}",
                endTime - startTime, updatedBecauseOfTariffZoneRefChange, updatedBecauseOfTopographicPlaceChange, stopsSavedTotal);


    }
}
