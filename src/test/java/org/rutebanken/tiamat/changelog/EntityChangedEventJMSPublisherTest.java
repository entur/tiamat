package org.rutebanken.tiamat.changelog;

import org.junit.Assert;
import org.junit.Test;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.ValidBetween;

import java.time.Instant;

public class EntityChangedEventJMSPublisherTest {

    private EntityChangedEventJMSPublisher jmsPublisher = new EntityChangedEventJMSPublisher();

    private static Instant NOW = Instant.now();

    @Test
    public void createdStopPlaceIsMappedToCreatedEvent() {
        StopPlace stopPlace = stopPlace(1l, NOW, null);

        EntityChangedEvent event = jmsPublisher.toEntityChangedEvent(stopPlace);
        Assert.assertEquals(EntityChangedEvent.EntityType.STOP_PLACE, event.entityType);
        Assert.assertEquals(stopPlace.getNetexId(), event.entityId);
        Assert.assertEquals(EntityChangedEvent.CrudAction.CREATE, event.crudAction);
    }


    @Test
    public void updatedStopPlaceIsMappedToUpdatedEvent() {
        StopPlace stopPlace = stopPlace(2l, NOW, null);

        EntityChangedEvent event = jmsPublisher.toEntityChangedEvent(stopPlace);
        Assert.assertEquals(EntityChangedEvent.EntityType.STOP_PLACE, event.entityType);
        Assert.assertEquals(stopPlace.getNetexId(), event.entityId);
        Assert.assertEquals(EntityChangedEvent.CrudAction.UPDATE, event.crudAction);
    }


    @Test
    public void deactivatedStopPlaceIsMappedToRemovedEvent() {
        StopPlace stopPlace = stopPlace(4l, NOW, NOW.plusMillis(2000));

        EntityChangedEvent event = jmsPublisher.toEntityChangedEvent(stopPlace);
        Assert.assertEquals(EntityChangedEvent.EntityType.STOP_PLACE, event.entityType);
        Assert.assertEquals(stopPlace.getNetexId(), event.entityId);
        Assert.assertEquals(EntityChangedEvent.CrudAction.REMOVE, event.crudAction);
    }

    private StopPlace stopPlace(Long version, Instant validFrom, Instant validTo) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("XXX:StopPlace:22");
        stopPlace.setVersion(version);
        stopPlace.getValidBetweens().add(new ValidBetween(validFrom, validTo));
        return stopPlace;
    }

}
