package org.rutebanken.tiamat.netex.id;

import org.eclipse.jetty.util.ConcurrentArrayQueue;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class GeneratedIdState {

    public static final int QUEUE_CAPACITY = 100;


    private final ConcurrentHashMap<String, BlockingQueue<Long>> availableIdsPerEntity = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Long> lastIdsPerEntity = new ConcurrentHashMap<>();

    /**
     * If an object claims to use a Netex ID. It should be inserted into the helper table.
     */
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> claimedIds = new ConcurrentHashMap<>();

    public GeneratedIdState () {
    }

    public void registerEntityTypeName(String entityTypeName, long startLastId) {
        availableIdsPerEntity.putIfAbsent(entityTypeName, new ArrayBlockingQueue<>(QUEUE_CAPACITY));
        claimedIds.putIfAbsent(entityTypeName, new ConcurrentLinkedQueue<>());
        lastIdsPerEntity.putIfAbsent(entityTypeName, startLastId);
    }

    public BlockingQueue<Long> getQueueForEntity(String entityTypeName) {
        return availableIdsPerEntity.get(entityTypeName);
    }

    public void setLastIdForEntity(String entityTypeName, long lastId) {
        lastIdsPerEntity.put(entityTypeName, lastId);
    }

    public long getLastIdForEntity(String entityTypeName) {
        return lastIdsPerEntity.get(entityTypeName);
    }

    public ConcurrentLinkedQueue<Long> getClaimedIdQueueForEntity(String entityTypeName) {
        return claimedIds.get(entityTypeName);
    }
}
