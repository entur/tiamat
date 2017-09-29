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

import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional
public class EntityChangedEventJMSPublisher implements EntityChangedListener {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${changelog.queue.name:IrkallaChangelogQueue}")
    private String queueName;

    @Value("${changelog.publish.enabled:true}")
    private boolean publish;

    @Override
    public void onChange(EntityInVersionStructure entity) {
        if (publish && isLoggedEntity(entity)) {
            jmsTemplate.convertAndSend(queueName, toEntityChangedEvent(entity, false).toString());
        }
    }

    @Override
    public void onDelete(EntityInVersionStructure entity) {
        if (publish && isLoggedEntity(entity)) {
            jmsTemplate.convertAndSend(queueName, toEntityChangedEvent(entity, true).toString());
        }
    }

    protected EntityChangedEvent toEntityChangedEvent(EntityInVersionStructure entity, boolean deleted) {
        EntityChangedEvent event = new EntityChangedEvent();
        event.msgId = UUID.randomUUID().toString();
        event.entityType = getEntityType(entity);
        event.entityId = entity.getNetexId();
        event.entityVersion = (entity).getVersion();

        if (deleted) {
            event.crudAction = EntityChangedEvent.CrudAction.DELETE;
        } else if (entity.getVersion() == 1) {
            event.crudAction = EntityChangedEvent.CrudAction.CREATE;
        } else if (isDeactivated(entity)) {
            event.crudAction = EntityChangedEvent.CrudAction.REMOVE;
        } else {
            event.crudAction = EntityChangedEvent.CrudAction.UPDATE;
        }

        return event;
    }


    private boolean isDeactivated(EntityInVersionStructure entity) {
        if (entity.getValidBetween() == null) {
            return false;
        }
        return entity.getValidBetween().getToDate() != null;
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
