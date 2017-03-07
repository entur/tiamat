package org.rutebanken.tiamat.netex.id;

import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.ConcurrentArrayQueue;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NetexIdProvider {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdProvider.class);

    private ConcurrentHashMap<String, BlockingQueue<String>> availableIds = new ConcurrentHashMap<>();

    /**
     * These IDs needs to be inserted
     */
    private ConcurrentHashMap<String, ConcurrentArrayQueue<String>> claimedIds = new ConcurrentHashMap<>();


    public String getId(IdentifiedEntity identifiedEntity) throws InterruptedException {
        return availableIds.get(identifiedEntity.getClass().getSimpleName()).take();
    }

    public void claimId(IdentifiedEntity identifiedEntity) {

        // Race conditions?

        if(!NetexIdMapper.isNsrId(identifiedEntity.getNetexId())) {
            throw new IdGeneratorException("Cannot claim ID " + identifiedEntity.getNetexId());
        }

        if(availableIds.get(key(identifiedEntity)).remove(identifiedEntity.getNetexId())) {
            logger.debug("ID: {} removed from list of available IDs", identifiedEntity.getNetexId());
        }

        // The ID was not in the list of available IDS.
        // Which means that it has to be inserted into the helper table.
        if(claimedIds.get(key(identifiedEntity)).add(identifiedEntity.getNetexId())) {
            logger.debug("ID {} added to list of claimed IDs", identifiedEntity.getNetexId());
        }

    }

    private String key(IdentifiedEntity identifiedEntity) {
        return identifiedEntity.getClass().getSimpleName();
    }
}
