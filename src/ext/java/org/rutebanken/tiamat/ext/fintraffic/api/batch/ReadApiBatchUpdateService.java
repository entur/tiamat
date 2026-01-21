package org.rutebanken.tiamat.ext.fintraffic.api.batch;

import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.ParkingSearch;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.rutebanken.tiamat.ext.fintraffic.api.ReadApiNetexMarshallingService;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityInRecord;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityStatus;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ReadApiBatchUpdateService {
    private final Logger logger = LoggerFactory.getLogger(ReadApiBatchUpdateService.class);

    private final ReadApiNetexMarshallingService marshallingService;
    private final StopPlaceRepository stopPlaceRepository;
    private final ParkingRepository parkingRepository;

    public ReadApiBatchUpdateService(
            ReadApiNetexMarshallingService marshallingService,
            StopPlaceRepository stopPlaceRepository,
            ParkingRepository parkingRepository) {
        this.marshallingService = marshallingService;
        this.stopPlaceRepository = stopPlaceRepository;
        this.parkingRepository = parkingRepository;
    }

    /**
     * Process StopPlaces in batches to avoid loading all entities into memory
     *
     * Note: This method must be transactional to keep the Hibernate ScrollableResult open
     * while iterating. The readOnly=true optimization tells Hibernate we're only reading data.
     *
     * @param batchSize Number of entities to process before flushing to database
     * @param batchConsumer Consumer that handles each batch (typically database upsert)
     * @return Statistics about the processing
     */
    @Transactional(readOnly = true)
    public ProcessingStats processStopPlacesInBatches(int batchSize, Consumer<List<ReadApiEntityInRecord>> batchConsumer) {
        StopPlaceSearch stopPlaceSearch = StopPlaceSearch.newStopPlaceSearchBuilder()
                .setAllVersions(false)
                .setSize(Integer.MAX_VALUE)
                .setVersionValidity(ExportParams.VersionValidity.CURRENT)
                .build();
        ExportParams exportParams = new ExportParams(stopPlaceSearch);

        Iterator<StopPlace> stopPlaceIterator = stopPlaceRepository.scrollStopPlaces(exportParams);
        return processEntitiesInBatches(stopPlaceIterator, batchSize, batchConsumer, "StopPlace");
    }

    /**
     * Process Parkings in batches to avoid loading all entities into memory
     *
     * Note: This method must be transactional to keep the Hibernate ScrollableResult open
     * while iterating. The readOnly=true optimization tells Hibernate we're only reading data.
     *
     * @param batchSize Number of entities to process before flushing to database
     * @param batchConsumer Consumer that handles each batch (typically database upsert)
     * @return Statistics about the processing
     */
    @Transactional(readOnly = true)
    public ProcessingStats processParkingsInBatches(int batchSize, Consumer<List<ReadApiEntityInRecord>> batchConsumer) {
        ParkingSearch parkingSearch = ParkingSearch.newParkingSearchBuilder()
                .setSize(Integer.MAX_VALUE)
                .setAllVersions(false)
                .build();

        Iterator<Parking> parkingIterator = parkingRepository.scrollParkings(parkingSearch);
        return processEntitiesInBatches(parkingIterator, batchSize, batchConsumer, "Parking");
    }

    /**
     * Generic method to process entities in batches
     */
    private <T extends EntityInVersionStructure> ProcessingStats processEntitiesInBatches(
            Iterator<T> iterator,
            int batchSize,
            Consumer<List<ReadApiEntityInRecord>> batchConsumer,
            String entityType) {

        Instant startTime = Instant.now();
        int processedCount = 0;
        int failedCount = 0;
        int totalRecords = 0;
        List<ReadApiEntityInRecord> batch = new ArrayList<>(batchSize);

        logger.info("Starting to process {} entities in batches of {}", entityType, batchSize);

        while (iterator.hasNext()) {
            T entity = iterator.next();
            processedCount++;

            try {
                Collection<ReadApiEntityInRecord> entityRecords =
                    marshallingService.createEntityRecords(entity, ReadApiEntityStatus.CURRENT);
                batch.addAll(entityRecords);
                totalRecords += entityRecords.size();

                // Log progress periodically
                if (processedCount % 500 == 0) {
                    logger.info("Processed {} {} entities so far ({} records generated, {} failed)",
                        processedCount, entityType, totalRecords, failedCount);
                }

                // Flush batch when it reaches the configured size
                if (batch.size() >= batchSize) {
                    batchConsumer.accept(batch);
                    logger.debug("Flushed batch of {} records to database", batch.size());
                    batch.clear(); // Release memory
                }

            } catch (Exception e) {
                failedCount++;
                logger.error("Failed to marshal {} entity with id: {}", entityType, entity.getNetexId(), e);
            }
        }

        // Process remaining entities in the last batch
        if (!batch.isEmpty()) {
            batchConsumer.accept(batch);
            logger.debug("Flushed final batch of {} records to database", batch.size());
        }

        Duration duration = Duration.between(startTime, Instant.now());
        logger.info("Completed processing {} {} entities in {}. Generated {} records total, {} entities failed",
            processedCount, entityType, formatDuration(duration), totalRecords, failedCount);

        return new ProcessingStats(processedCount, totalRecords, failedCount, duration);
    }

    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        } else {
            return (seconds / 3600) + "h " + ((seconds % 3600) / 60) + "m";
        }
    }

    /**
     * Statistics about entity processing
     */
    public record ProcessingStats(
        int entitiesProcessed,
        int recordsGenerated,
        int entitiesFailed,
        Duration duration
    ) { }
}
