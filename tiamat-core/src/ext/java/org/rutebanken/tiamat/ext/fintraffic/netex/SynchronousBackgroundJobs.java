package org.rutebanken.tiamat.ext.fintraffic.netex;

import org.rutebanken.tiamat.service.batch.BackgroundJobs;
import org.rutebanken.tiamat.service.batch.StopPlaceRefUpdaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link BackgroundJobs} for the NeTEx import task. The task calls {@code System.exit} when
 * the import returns, which stops any job that still runs on an executor.
 * <p>
 * {@link #triggerStopPlaceUpdate()} runs on the calling thread. The importers call it, so the
 * stop place references are rebuilt before the task reports {@code done}.
 * <p>
 * {@link #scheduleBackgroundJobs()} schedules nothing. The periodic update starts one minute
 * after startup. It would hold {@code BACKGROUND_UPDATE_STOPS_LOCK}, and the synchronous call
 * would give up after the ten second lock wait.
 */
public class SynchronousBackgroundJobs extends BackgroundJobs {

    private static final Logger logger = LoggerFactory.getLogger(SynchronousBackgroundJobs.class);

    private final StopPlaceRefUpdaterService stopPlaceRefUpdaterService;

    public SynchronousBackgroundJobs(StopPlaceRefUpdaterService stopPlaceRefUpdaterService) {
        super(stopPlaceRefUpdaterService);
        this.stopPlaceRefUpdaterService = stopPlaceRefUpdaterService;
    }

    @Override
    public void scheduleBackgroundJobs() {
        logger.info("Not scheduling the periodic stop place reference update. The import task runs it synchronously");
    }

    @Override
    public void triggerStopPlaceUpdate() {
        logger.info("Updating stop place references synchronously on thread {}", Thread.currentThread().getName());
        stopPlaceRefUpdaterService.updateAllStopPlaces();
    }
}
