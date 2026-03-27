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

package org.rutebanken.tiamat.netex.id;

import com.hazelcast.collection.IQueue;
import com.hazelcast.collection.ISet;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import static org.rutebanken.tiamat.netex.id.GaplessIdGeneratorService.INITIAL_LAST_ID;

@Service
public class GeneratedIdState implements Serializable{

    public static final String LAST_IDS_FOR_ENTITY = "lastIdsForEntities";
    public static final String CLAIMED_IDS_FOR_ENTITY_PREFIX = "claimedIdsForEntities";
    public static final String ENTITY_NAMES_REGISTERED = "entityNamesRegistered";

    private final HazelcastInstance hazelcastInstance;
    private final NetexIdRangeConfiguration netexIdRangeConfiguration;

    @Autowired
    public GeneratedIdState(HazelcastInstance hazelcastInstance, NetexIdRangeConfiguration netexIdRangeConfiguration) {
        this.hazelcastInstance = hazelcastInstance;
        this.netexIdRangeConfiguration = netexIdRangeConfiguration;
    }


    public IQueue getQueueForEntity(String entityTypeName) {
        hazelcastInstance.getSet(ENTITY_NAMES_REGISTERED).add(entityTypeName);
        return hazelcastInstance.getQueue(entityTypeName);
    }

    public void setLastIdForEntity(String entityTypeName, long lastId) {
        hazelcastInstance.getMap(LAST_IDS_FOR_ENTITY).put(entityTypeName, lastId);
    }

    public Set<String> getRegisteredEntityNames() {
        return hazelcastInstance.getSet(ENTITY_NAMES_REGISTERED);
    }

    /**
     * Last generated id for entity.
     * If no value for entity, uses the configured range minimum minus one
     * (so the first generated ID will be exactly the range minimum),
     * or falls back to {@link GaplessIdGeneratorService#INITIAL_LAST_ID}.
     *
     * @param entityTypeName the entity type name
     * @return the last generated id.
     */
    public long getLastIdForEntity(String entityTypeName) {
        ConcurrentMap<String, Long> lastIdMap = hazelcastInstance.getMap(LAST_IDS_FOR_ENTITY);
        long initialId = netexIdRangeConfiguration.getRangeForEntity(entityTypeName)
                .map(range -> range.getMin() - 1)
                .orElse(INITIAL_LAST_ID);
        lastIdMap.putIfAbsent(entityTypeName, initialId);
        return lastIdMap.get(entityTypeName);
    }

    public ISet getClaimedIdListForEntity(String entityTypeName) {
        return hazelcastInstance.getSet(CLAIMED_IDS_FOR_ENTITY_PREFIX + "-" + entityTypeName);
    }
}
