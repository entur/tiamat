package org.rutebanken.tiamat.ext.fintraffic.netex;

import org.rutebanken.tiamat.netex.id.GaplessIdGeneratorService;
import org.rutebanken.tiamat.service.batch.BackgroundJobs;
import org.rutebanken.tiamat.service.batch.StopPlaceRefUpdaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * {@link BackgroundJobs} for the NeTEx import task, which calls {@code System.exit} as soon as
 * the import returns — killing any job still queued on an executor mid-transaction.
 * <p>
 * {@link BackgroundJobs#triggerStopPlaceUpdate()} runs on the calling thread instead, so the
 * stop place references are rebuilt inside {@code importPublicationDelivery} and before the
 * task reports {@code done}. No import transaction is widened: none of the three importers is
 * transactional, so the update opens its own.
 * <p>
 * The periodic stop place update is dropped, not just made redundant: its first run fires one
 * minute after startup and would take {@code BACKGROUND_UPDATE_STOPS_LOCK} from the
 * synchronous call, which then gives up after the ten second lock timeout. The gapless id
 * persister is kept, since dropping it would make the loss of claimed NeTEx id ranges certain
 * rather than merely possible — {@code System.exit} already truncates its last window.
 * <p>
 * Scoped to the import task profile. {@code TariffZoneTerminator} triggers the same update
 * from inside a sixty second mutate lock, which an inline rebuild would outlast.
 */
public class SynchronousBackgroundJobs extends BackgroundJobs {

    private static final Logger logger = LoggerFactory.getLogger(SynchronousBackgroundJobs.class);

    private static final int PERSIST_CLAIMED_IDS_INTERVAL_SECONDS = 15;

    private final GaplessIdGeneratorService gaplessIdGeneratorService;
    private final StopPlaceRefUpdaterService stopPlaceRefUpdaterService;

    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "import-task-background-job");
                // Daemon, unlike tiamat-core's pool; the task ends via System.exit either way.
                thread.setDaemon(true);
                return thread;
            });

    public SynchronousBackgroundJobs(GaplessIdGeneratorService gaplessIdGeneratorService,
                                     StopPlaceRefUpdaterService stopPlaceRefUpdaterService) {
        super(gaplessIdGeneratorService, stopPlaceRefUpdaterService);
        this.gaplessIdGeneratorService = gaplessIdGeneratorService;
        this.stopPlaceRefUpdaterService = stopPlaceRefUpdaterService;
    }

    @Override
    public void scheduleBackgroundJobs() {
        logger.info("Scheduling background job for gaplessIdGeneratorService");
        executor.scheduleAtFixedRate(gaplessIdGeneratorService::persistClaimedIds,
                PERSIST_CLAIMED_IDS_INTERVAL_SECONDS, PERSIST_CLAIMED_IDS_INTERVAL_SECONDS, TimeUnit.SECONDS);
        logger.info("Not scheduling the periodic stop place reference update; the import task runs it synchronously");
    }

    @Override
    public void triggerStopPlaceUpdate() {
        logger.info("Updating stop place references synchronously on thread {}", Thread.currentThread().getName());
        stopPlaceRefUpdaterService.updateAllStopPlaces();
    }
}
