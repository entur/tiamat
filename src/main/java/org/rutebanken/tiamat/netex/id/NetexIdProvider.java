package org.rutebanken.tiamat.netex.id;

import com.hazelcast.core.HazelcastInstance;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;

@Component
public class NetexIdProvider {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdProvider.class);

    private final GeneratedIdState generatedIdState;

    private final HazelcastInstance hazelcastInstance;

    private final GaplessIdGenerator gaplessIdGenerator;

    @Autowired
    public NetexIdProvider(GeneratedIdState generatedIdState, HazelcastInstance hazelcastInstance, GaplessIdGenerator gaplessIdGenerator) {
        this.generatedIdState = generatedIdState;
        this.hazelcastInstance = hazelcastInstance;
        this.gaplessIdGenerator = gaplessIdGenerator;
    }

    public String getGeneratedId(IdentifiedEntity identifiedEntity) throws InterruptedException {
        String entityTypeName = key(identifiedEntity);

        List<Long> claimedIds = generatedIdState.getClaimedIdListForEntity(entityTypeName);
        BlockingQueue<Long> availableIds = generatedIdState.getQueueForEntity(entityTypeName);

        executeInLock(() -> availableIds.removeAll(claimedIds), entityTypeName);

        long longId = gaplessIdGenerator.getNextIdForEntity(entityTypeName);

        return NetexIdMapper.getNetexId(entityTypeName, String.valueOf(longId));
    }

    public void claimId(IdentifiedEntity identifiedEntity) {

        if (!NetexIdMapper.isNsrId(identifiedEntity.getNetexId())) {
            logger.warn("Detected non NSR ID: " + identifiedEntity.getNetexId());
        } else {
            Long longId = NetexIdMapper.getNetexIdPostfix(identifiedEntity.getNetexId());

            String entityTypeName = key(identifiedEntity);
            BlockingQueue<Long> availableIds = generatedIdState.getQueueForEntity(entityTypeName);

            executeInLock(() -> {
                if (availableIds.remove(longId)) {
                    logger.debug("ID: {} removed from list of available IDs", identifiedEntity.getNetexId());
                    // If the claimed ID was already in the list of avaiable IDs, it has already been inserted.
                    // Do not add it to the list of claimed IDs, because then it will be attempted inserted again.
                } else if (generatedIdState.getClaimedIdListForEntity(entityTypeName).add(longId)) {
                    logger.debug("ID {} added to list of claimed IDs", identifiedEntity.getNetexId());
                }
            }, entityTypeName);
        }
    }

    private void executeInLock(Runnable runnable, String entityTypeName) {
        Lock lock = hazelcastInstance.getLock(GaplessIdGeneratorTask.entityLockString(entityTypeName));
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    private String key(IdentifiedEntity identifiedEntity) {
        return identifiedEntity.getClass().getSimpleName();
    }
}
