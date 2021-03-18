/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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

        EntityChangedEvent event = jmsPublisher.toEntityChangedEvent(stopPlace, false);
        Assert.assertEquals(EntityChangedEvent.EntityType.STOP_PLACE, event.entityType);
        Assert.assertEquals(stopPlace.getNetexId(), event.entityId);
        Assert.assertEquals(EntityChangedEvent.CrudAction.CREATE, event.crudAction);
    }


    @Test
    public void updatedStopPlaceIsMappedToUpdatedEvent() {
        StopPlace stopPlace = stopPlace(2l, NOW, null);

        EntityChangedEvent event = jmsPublisher.toEntityChangedEvent(stopPlace, false);
        Assert.assertEquals(EntityChangedEvent.EntityType.STOP_PLACE, event.entityType);
        Assert.assertEquals(stopPlace.getNetexId(), event.entityId);
        Assert.assertEquals(EntityChangedEvent.CrudAction.UPDATE, event.crudAction);
    }


    @Test
    public void deactivatedStopPlaceIsMappedToRemovedEvent() {
        StopPlace stopPlace = stopPlace(4l, NOW, NOW.plusMillis(2000));

        EntityChangedEvent event = jmsPublisher.toEntityChangedEvent(stopPlace, false);
        Assert.assertEquals(EntityChangedEvent.EntityType.STOP_PLACE, event.entityType);
        Assert.assertEquals(stopPlace.getNetexId(), event.entityId);
        Assert.assertEquals(EntityChangedEvent.CrudAction.REMOVE, event.crudAction);
    }


    @Test
    public void deletedStopPlaceIsMappedToDeleteEvent() {
        StopPlace stopPlace = stopPlace(1l, NOW, null);

        EntityChangedEvent event = jmsPublisher.toEntityChangedEvent(stopPlace, true);
        Assert.assertEquals(EntityChangedEvent.EntityType.STOP_PLACE, event.entityType);
        Assert.assertEquals(stopPlace.getNetexId(), event.entityId);
        Assert.assertEquals(EntityChangedEvent.CrudAction.DELETE, event.crudAction);
    }


    private StopPlace stopPlace(Long version, Instant validFrom, Instant validTo) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("XXX:StopPlace:22");
        stopPlace.setVersion(version);
        stopPlace.setValidBetween(new ValidBetween(validFrom, validTo));
        return stopPlace;
    }

}
