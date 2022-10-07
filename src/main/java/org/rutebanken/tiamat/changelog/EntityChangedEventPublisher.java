package org.rutebanken.tiamat.changelog;

import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.StopPlace;

import java.util.UUID;

public abstract class EntityChangedEventPublisher {
    protected boolean isLoggedEntity(EntityStructure entity) {
        return getEntityType(entity) != null;
    }

    protected EntityChangedEvent toEntityChangedEvent(EntityInVersionStructure entity, boolean deleted) {
        EntityChangedEvent event = new EntityChangedEvent();
        event.msgId = UUID.randomUUID().toString();
        event.entityType = getEntityType(entity);
        event.entityId = entity.getNetexId();
        event.entityVersion = (entity).getVersion();
        event.entityChanged = getEntityChangedAsEpochMillis(entity);
        event.crudAction = getCrudAction(entity, deleted);
        return event;
    }

    private Long getEntityChangedAsEpochMillis(EntityInVersionStructure entity) {
        if (entity.getChanged() != null) {
            return entity.getChanged().toEpochMilli();
        } else {
            return null;
        }
    }

    private EntityChangedEvent.CrudAction getCrudAction(EntityInVersionStructure entity, boolean deleted) {
        if (deleted) {
            return EntityChangedEvent.CrudAction.DELETE;
        } else if (entity.getVersion() == 1) {
            return EntityChangedEvent.CrudAction.CREATE;
        } else if (isDeactivated(entity)) {
            return EntityChangedEvent.CrudAction.REMOVE;
        } else {
            return EntityChangedEvent.CrudAction.UPDATE;
        }
    }

    private boolean isDeactivated(EntityInVersionStructure entity) {
        if (entity.getValidBetween() == null) {
            return false;
        }
        return entity.getValidBetween().getToDate() != null;
    }

    private EntityChangedEvent.EntityType getEntityType(EntityStructure entity) {
        if (entity instanceof StopPlace) {
            return EntityChangedEvent.EntityType.STOP_PLACE;
        }
        return null;
    }
}
