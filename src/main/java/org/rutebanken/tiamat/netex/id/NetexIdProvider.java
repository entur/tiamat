package org.rutebanken.tiamat.netex.id;

import org.eclipse.jetty.util.ConcurrentArrayQueue;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NetexIdProvider {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdProvider.class);

     private GeneratedIdState generatedIdState;

    @Autowired
    public NetexIdProvider(GeneratedIdState generatedIdState) {
        this.generatedIdState = generatedIdState;
    }


    public String getGeneratedId(IdentifiedEntity identifiedEntity) throws InterruptedException {
        String entityTypeName = identifiedEntity.getClass().getSimpleName();
        long longId = generatedIdState.getQueueForEntity(entityTypeName).take();
        return NetexIdMapper.getNetexId(entityTypeName, String.valueOf(longId));
    }

    public void claimId(IdentifiedEntity identifiedEntity) {

        // Race conditions?

        if(!NetexIdMapper.isNsrId(identifiedEntity.getNetexId())) {
            throw new IdGeneratorException("Cannot claim ID " + identifiedEntity.getNetexId());
        }

        if(generatedIdState.getQueueForEntity(key(identifiedEntity)).remove(identifiedEntity.getNetexId())) {
            logger.debug("ID: {} removed from list of available IDs", identifiedEntity.getNetexId());
        }

        // The ID was not in the list of available IDS.
        // Which means that it has to be inserted into the helper table.
        Long longId = NetexIdMapper.getNetexIdPostfix(identifiedEntity.getNetexId());
        if(generatedIdState.getClaimedIdQueueForEntity(key(identifiedEntity)).add(longId)) {
            logger.debug("ID {} added to list of claimed IDs", identifiedEntity.getNetexId());
        }

    }

    private String key(IdentifiedEntity identifiedEntity) {
        return identifiedEntity.getClass().getSimpleName();
    }
}
