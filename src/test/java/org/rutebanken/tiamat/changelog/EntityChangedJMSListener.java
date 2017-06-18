package org.rutebanken.tiamat.changelog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Receive entity changed messages sent from tiamat for verifying tests.
 */
@Component
public class EntityChangedJMSListener {

    private static Set<EntityChangedEvent> events = new HashSet<>();


    private final Logger logger = LoggerFactory.getLogger(getClass());


    @JmsListener(destination = "IrkallaChangelogQueue")
    public void receiveEntityChangedMessage(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            events.add(mapper.readValue(message, EntityChangedEvent.class));
        } catch (IOException e) {
            logger.warn("Failed to process entity changed message: " + message);
        }

    }

    public boolean hasReceivedEvent(String entityId, Long version, EntityChangedEvent.CrudAction crudAction) {
        return events.stream().anyMatch(e -> isMatch(e, entityId, version, crudAction));
    }

    private boolean isMatch(EntityChangedEvent event, String entityId, Long version, EntityChangedEvent.CrudAction crudAction) {
        if (entityId != null && !entityId.equals(event.entityId)) {
            return false;
        }
        if (version != null && !version.equals(event.entityVersion)) {
            return false;
        }
        if (crudAction != null && !crudAction.equals(event.crudAction)) {
            return false;
        }

        return true;
    }


    public Set<EntityChangedEvent> popEvents() {
        Set<EntityChangedEvent> retEvents = new HashSet<>(events);
        events.clear();
        return retEvents;
    }
}
