package org.rutebanken.tiamat.changelog;

import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EntityChangedEventJMSPublisher implements EntityChangedListener {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${changelog.queue.name:IrkallaChangelogQueue}")
    private String queueName;

    @Value("${changelog.publish.enabled:false}")
    private boolean publish;

    @Override
    public void onChange(EntityInVersionStructure entity) {
        if (publish && isLoggedEntity(entity)) {
            jmsTemplate.convertAndSend(queueName, toEntityChangedEvent(entity).toString());
        }
    }

    protected EntityChangedEvent toEntityChangedEvent(EntityInVersionStructure entity) {
        EntityChangedEvent event = new EntityChangedEvent();
        event.msgId = UUID.randomUUID().toString();
        event.entityType = getEntityType(entity);
        event.entityId = entity.getNetexId();
        event.entityVersion = (entity).getVersion();

        if (entity.getVersion() == 1) {
            event.crudAction = EntityChangedEvent.CrudAction.CREATE;
        } else if (isDeactivated(entity)) {
            event.crudAction = EntityChangedEvent.CrudAction.REMOVE;
        } else {
            event.crudAction = EntityChangedEvent.CrudAction.UPDATE;
        }

        return event;
    }

    private boolean isDeactivated(EntityInVersionStructure entity) {
        if (entity.getValidBetweens() == null) {
            return false;
        }
        return entity.getValidBetweens().stream().allMatch(vb -> vb.getToDate() != null);
    }

    private boolean isLoggedEntity(EntityStructure entity) {
        return getEntityType(entity) != null;
    }

    private EntityChangedEvent.EntityType getEntityType(EntityStructure entity) {
        if (entity instanceof StopPlace) {
            return EntityChangedEvent.EntityType.STOP_PLACE;
        }
        return null;
    }
}
