package org.rutebanken.tiamat.ext.fintraffic.api;

import org.rutebanken.tiamat.changelog.EntityChangedEvent;
import org.rutebanken.tiamat.changelog.EntityChangedEventPublisher;
import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Transactional
public class ReadApiEntityChangedPublisher extends EntityChangedEventPublisher implements EntityChangedListener {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ReadApiEntityChangedPublisher.class);

    private final ReadApiNetexMarshallingService readApiNetexMarshallingService;

    public ReadApiEntityChangedPublisher(
            ReadApiNetexMarshallingService readApiNetexMarshallingService
    ) {
        this.readApiNetexMarshallingService = readApiNetexMarshallingService;
    }

    @Override
    public void onChange(EntityInVersionStructure entity) {
        logger.info("Received entity changed event for entity: {} version: {}", entity.getNetexId(), entity.getVersion());
        Instant now = Instant.now();
        EntityChangedEvent event = super.toEntityChangedEvent(entity, false);
        boolean updated = readApiNetexMarshallingService.handleEntityChange(entity, event);
        Instant end = Instant.now();
        logResult(updated, "changed", entity, end.toEpochMilli() - now.toEpochMilli());
        if (entity instanceof StopPlace stopPlace && stopPlace.getChildren() != null && !stopPlace.getChildren().isEmpty()) {
            // Handle child entities if needed
            stopPlace.getChildren().forEach(this::onChange);
        }
    }

    @Override
    public void onDelete(EntityInVersionStructure entity) {
        logger.info("Received entity deleted event for entity: {} version: {}", entity.getNetexId(), entity.getVersion());
        Instant now = Instant.now();
        boolean updated;
        if (entity instanceof Parking parking) {
            updated = readApiNetexMarshallingService.handleParkingDeleted(parking);
        } else {
            EntityChangedEvent event = super.toEntityChangedEvent(entity, true);
            updated = readApiNetexMarshallingService.handleEntityChange(entity, event);
        }
        Instant end = Instant.now();
        logResult(updated, "deleted", entity, end.toEpochMilli() - now.toEpochMilli());
    }

    private static void logResult(boolean updated, String kind, EntityInVersionStructure entity, long millis) {
        if (updated) {
            logger.info("Processed entity {} event for entity: {} version: {} in {} ms", kind, entity.getNetexId(), entity.getVersion(), millis);
        } else {
            logger.error("Failed to process entity {} event for entity: {} type: {} version: {} after {} ms",
                    kind, entity.getNetexId(), entity.getClass().getSimpleName(), entity.getVersion(), millis);
        }
    }
}
