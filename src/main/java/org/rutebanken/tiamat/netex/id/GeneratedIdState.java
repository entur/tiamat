package org.rutebanken.tiamat.netex.id;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.*;

import static org.rutebanken.tiamat.netex.id.GaplessIdGeneratorService.INITIAL_LAST_ID;

@Service
public class GeneratedIdState implements Serializable{

    public static final String LAST_IDS_FOR_ENTITY = "lastIdsForEntities";
    public static final String CLAIMED_IDS_FOR_ENTITY_PREFIX = "claimedIdsForEntities";

    private final HazelcastInstance hazelcastInstance;

    @Autowired
    public GeneratedIdState(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }


    public IQueue<Long> getQueueForEntity(String entityTypeName) {
        return hazelcastInstance.getQueue(entityTypeName);
    }

    public void setLastIdForEntity(String entityTypeName, long lastId) {
        hazelcastInstance.getMap(LAST_IDS_FOR_ENTITY).put(entityTypeName, lastId);
    }

    /**
     * Last generated id for entity
     * If no value for entity, INITIAL_LAST_ID is set.
     *
     * @param entityTypeName
     * @return the last generated id.
     */
    public long getLastIdForEntity(String entityTypeName) {
        ConcurrentMap<String, Long> lastIdMap = hazelcastInstance.getMap(LAST_IDS_FOR_ENTITY);
        lastIdMap.putIfAbsent(entityTypeName, INITIAL_LAST_ID);
        return lastIdMap.get(entityTypeName);
    }

    public List<Long> getClaimedIdListForEntity(String entityTypeName) {
        return hazelcastInstance.getList(CLAIMED_IDS_FOR_ENTITY_PREFIX + "-" + entityTypeName);
    }
}
