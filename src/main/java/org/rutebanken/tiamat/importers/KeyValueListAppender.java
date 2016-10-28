package org.rutebanken.tiamat.importers;

import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.netexmapping.NetexIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KeyValueListAppender {

    private static final Logger logger = LoggerFactory.getLogger(KeyValueListAppender.class);

    public boolean appendToOriginalId(String key, DataManagedObjectStructure newObject, DataManagedObjectStructure existingObject) {

        List<String> existingObjectIds = existingObject.getOrCreateValues(key);
        List<String> newObjectIds = newObject.getOrCreateValues(key);

        boolean changed = false;
        for(String newOriginalId : newObjectIds) {
            if(!existingObjectIds.contains(newOriginalId)) {
                logger.info("Adding new original ID '{}' to existing object {}", newOriginalId, existingObject);
                existingObjectIds.add(newOriginalId);
                changed = true;
            }
        }
        return changed;
    }
}
