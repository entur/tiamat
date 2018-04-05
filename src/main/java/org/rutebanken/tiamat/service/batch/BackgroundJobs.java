package org.rutebanken.tiamat.service.batch;

import org.rutebanken.tiamat.netex.id.GaplessIdGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Jobs that run periodically in the background
 */
@Service
public class BackgroundJobs {

    private static final Logger logger = LoggerFactory.getLogger(BackgroundJobs.class);

    private final ScheduledExecutorService backgroundJobExecutor =
            Executors.newScheduledThreadPool(2, (runnable) -> new Thread(runnable, "background-job"));

    private final GaplessIdGeneratorService gaplessIdGeneratorService;

    private final StopPlaceRefUpdaterService stopPlaceRefUpdaterService;

    @Autowired
    public BackgroundJobs(GaplessIdGeneratorService gaplessIdGeneratorService, StopPlaceRefUpdaterService stopPlaceRefUpdaterService) {
        this.gaplessIdGeneratorService = gaplessIdGeneratorService;
        this.stopPlaceRefUpdaterService = stopPlaceRefUpdaterService;
    }

    @PostConstruct
    public void scheduleBackgroundJobs() {
        logger.info("Scheduling background job for gaplessIdGeneratorService");
        backgroundJobExecutor.scheduleAtFixedRate(gaplessIdGeneratorService::persistClaimedIds, 15, 15, TimeUnit.SECONDS);

        // Initial delay for the background stop place reference updater service can be good to avoid conflicty when running tests
        logger.info("Scheduling background job for updating stop places");
        backgroundJobExecutor.scheduleAtFixedRate(stopPlaceRefUpdaterService::updateAllStopPlaces, 0, 1, TimeUnit.MINUTES);
    }
}
