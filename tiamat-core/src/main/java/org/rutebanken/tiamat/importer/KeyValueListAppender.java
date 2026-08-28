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

package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class KeyValueListAppender {

    private static final Logger logger = LoggerFactory.getLogger(KeyValueListAppender.class);

    public boolean appendToOriginalId(String key, DataManagedObjectStructure newObject, DataManagedObjectStructure existingObject) {

        Set<String> existingObjectIds = existingObject.getOrCreateValues(key);
        Set<String> newObjectIds = newObject.getOrCreateValues(key);

        boolean changed = false;
        for(String newOriginalId : newObjectIds) {
            if(!existingObjectIds.contains(newOriginalId)) {
                logger.debug("Adding new original ID '{}' to existing object {}", newOriginalId, existingObject);
                existingObjectIds.add(newOriginalId);
                changed = true;
            }
        }
        return changed;
    }
}
