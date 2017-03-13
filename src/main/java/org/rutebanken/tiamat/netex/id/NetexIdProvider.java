package org.rutebanken.tiamat.netex.id;

import org.eclipse.jetty.util.ConcurrentArrayQueue;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class NetexIdProvider {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdProvider.class);

     private GeneratedIdState generatedIdState;

    @Autowired
    public NetexIdProvider(GeneratedIdState generatedIdState) {
        this.generatedIdState = generatedIdState;
    }


    public String getGeneratedId(IdentifiedEntity identifiedEntity) throws InterruptedException {
        String entityTypeName = key(identifiedEntity);

        List<Long> claimedIds = generatedIdState.getClaimedIdQueueForEntity(entityTypeName);
        BlockingQueue<Long> availableIds = generatedIdState.getQueueForEntity(entityTypeName);
        availableIds.removeAll(claimedIds);
        long longId = availableIds.take();

        return NetexIdMapper.getNetexId(entityTypeName, String.valueOf(longId));
    }

    public void claimId(IdentifiedEntity identifiedEntity) {

        if(!NetexIdMapper.isNsrId(identifiedEntity.getNetexId())) {
            logger.warn("Detected non NSR ID: " + identifiedEntity.getNetexId());
        } else {
            Long longId = NetexIdMapper.getNetexIdPostfix(identifiedEntity.getNetexId());

            BlockingQueue<Long> availableIds = generatedIdState.getQueueForEntity(key(identifiedEntity));

            if (availableIds.remove(longId)) {
                logger.debug("ID: {} removed from list of available IDs", identifiedEntity.getNetexId());
            }

            // The ID was not in the list of available IDS.
            // Which means that it has to be inserted into the helper table.

            if (generatedIdState.getClaimedIdQueueForEntity(key(identifiedEntity)).add(longId)) {
                logger.debug("ID {} added to list of claimed IDs", identifiedEntity.getNetexId());
            }
        }
    }

    private String key(IdentifiedEntity identifiedEntity) {
        return identifiedEntity.getClass().getSimpleName();
    }
}
