package org.rutebanken.tiamat.changelog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.StringWriter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityChangedEvent {

    public enum EntityType {STOP_PLACE}

    public enum CrudAction {CREATE, UPDATE, REMOVE, DELETE}

    public String msgId;

    public EntityType entityType;

    public String entityId;

    public Long entityVersion;

    public CrudAction crudAction;

    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, this);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
