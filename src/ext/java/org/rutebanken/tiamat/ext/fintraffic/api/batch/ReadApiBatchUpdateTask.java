package org.rutebanken.tiamat.ext.fintraffic.api.batch;

import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.time.Duration;
import java.time.Instant;

/**
 * ApplicationRunner that executes the Fintraffic Read API update process on startup.
 * This task will:
 * 1. Mark all existing entities as STALE
 * 2. Fetch and process new data from Tiamat in batches (streaming, not loading everything into memory)
 * 3. Update the ext_fintraffic_netex_entity table with current data
 * 4. Clean up stale entities that were not updated
 * 5. Shut down the application when complete
 */
public class ReadApiBatchUpdateTask implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(ReadApiBatchUpdateTask.class);
    private static final int DEFAULT_BATCH_SIZE = 1000;

    private final NetexRepository netexRepository;
    private final ApplicationContext applicationContext;
    private final ReadApiBatchUpdateService readApiBatchUpdateService;
    private final ReadApiBatchWriteService readApiBatchWriteService;

    @Autowired
    public ReadApiBatchUpdateTask(
            NetexRepository netexRepository,
            ApplicationContext applicationContext,
            ReadApiBatchUpdateService readApiBatchUpdateService,
            ReadApiBatchWriteService readApiBatchWriteService) {
        this.netexRepository = netexRepository;
        this.applicationContext = applicationContext;
        this.readApiBatchUpdateService = readApiBatchUpdateService;
        this.readApiBatchWriteService = readApiBatchWriteService;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Starting Fintraffic Read API update task with batch size: {}", DEFAULT_BATCH_SIZE);
        Instant startTime = Instant.now();

        int totalEntitiesProcessed = 0;
        int totalRecordsGenerated = 0;
        int totalEntitiesFailed = 0;

        try {
            // Step 1: Mark all rows as STALE
            logger.info("Step 1/4: Marking all entities as STALE");
            int markedStale = netexRepository.markAllEntitiesAsStale();
            logger.info("Marked {} entities as STALE", markedStale);

            // Step 2: Process StopPlaces in batches (streaming)
            logger.info("Step 2/4: Processing StopPlaces (this may take 1-2 hours)");
            ReadApiBatchUpdateService.ProcessingStats stopPlaceStats =
                readApiBatchUpdateService.processStopPlacesInBatches(DEFAULT_BATCH_SIZE, batch -> {
                    readApiBatchWriteService.upsertBatch(batch);
                    logger.debug("Upserted batch of {} StopPlace records", batch.size());
                });

            totalEntitiesProcessed += stopPlaceStats.entitiesProcessed();
            totalRecordsGenerated += stopPlaceStats.recordsGenerated();
            totalEntitiesFailed += stopPlaceStats.entitiesFailed();

            logger.info("StopPlace processing completed: {} entities → {} records ({} failed) in {}",
                stopPlaceStats.entitiesProcessed(),
                stopPlaceStats.recordsGenerated(),
                stopPlaceStats.entitiesFailed(),
                formatDuration(stopPlaceStats.duration()));

            // Step 3: Process Parkings in batches (streaming)
            logger.info("Step 3/4: Processing Parkings");
            ReadApiBatchUpdateService.ProcessingStats parkingStats =
                readApiBatchUpdateService.processParkingsInBatches(DEFAULT_BATCH_SIZE, batch -> {
                    readApiBatchWriteService.upsertBatch(batch);
                    logger.debug("Upserted batch of {} Parking records", batch.size());
                });

            totalEntitiesProcessed += parkingStats.entitiesProcessed();
            totalRecordsGenerated += parkingStats.recordsGenerated();
            totalEntitiesFailed += parkingStats.entitiesFailed();

            logger.info("Parking processing completed: {} entities → {} records ({} failed) in {}",
                parkingStats.entitiesProcessed(),
                parkingStats.recordsGenerated(),
                parkingStats.entitiesFailed(),
                formatDuration(parkingStats.duration()));

            // Step 4: Clean up deleted entities
            logger.info("Step 4/4: Cleaning up stale entities");
            int deletedCount = -1;
            if (totalEntitiesFailed > 0) {
                logger.warn("There were {} failed entities during processing. " +
                        "Please check the logs to ensure no data inconsistencies.",
                    totalEntitiesFailed);
                logger.warn("Unable to remove stale entities until all processing errors are resolved.");
            } else {
                deletedCount = netexRepository.removeStaleEntities();
                logger.info("Removed {} stale entities", deletedCount);
            }

            Duration totalDuration = Duration.between(startTime, Instant.now());
            logger.info("""
                ════════════════════════════════════════════════════════════
                Fintraffic Read API update completed {}!
                ────────────────────────────────────────────────────────────
                Total entities processed: {}
                Total records generated:  {}
                Failed entities:          {}
                Stale entities removed:   {}
                Total duration:           {}
                ════════════════════════════════════════════════════════════
                """,
                totalEntitiesFailed > 0 ? "with errors" : "successfully",
                totalEntitiesProcessed,
                totalRecordsGenerated,
                totalEntitiesFailed,
                deletedCount,
                formatDuration(totalDuration));

        } catch (Exception e) {
            Duration failedDuration = Duration.between(startTime, Instant.now());
            logger.error("""
                ════════════════════════════════════════════════════════════
                Fintraffic Read API update FAILED after {}
                ────────────────────────────────────────────────────────────
                Entities processed before failure: {}
                Records generated before failure:  {}
                ════════════════════════════════════════════════════════════
                """,
                formatDuration(failedDuration),
                totalEntitiesProcessed,
                totalRecordsGenerated,
                e);
            System.exit(1);
        } finally {
            // Shut down the application
            logger.info("Shutting down application...");
            System.exit(SpringApplication.exit(applicationContext, () -> 0));
        }
    }

    /**
     * Formats a duration into a human-readable string.
     * <p>
     * Examples:
     * <ul>
     *   <li>PT2H30M45S → "2h 30m 45s"</li>
     *   <li>PT15M30S → "15m 30s"</li>
     *   <li>PT42S → "42s"</li>
     * </ul>
     *
     * @param duration the duration to format
     * @return formatted string with appropriate time units
     */
    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
}
