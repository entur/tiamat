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


    private final GaplessIdGeneratorService gaplessIdGenerator;

    @Autowired
    public NetexIdProvider(GaplessIdGeneratorService gaplessIdGenerator) {
        this.gaplessIdGenerator = gaplessIdGenerator;
    }

    public String getGeneratedId(IdentifiedEntity identifiedEntity) throws InterruptedException {
        String entityTypeName = key(identifiedEntity);

        long longId = gaplessIdGenerator.getNextIdForEntity(entityTypeName);

        return NetexIdMapper.getNetexId(entityTypeName, String.valueOf(longId));
    }

    public void claimId(IdentifiedEntity identifiedEntity) {

        if (!NetexIdMapper.isNsrId(identifiedEntity.getNetexId())) {
            logger.warn("Detected non NSR ID: {}", identifiedEntity.getNetexId());
        } else {
            Long claimedId = NetexIdMapper.getNetexIdPostfix(identifiedEntity.getNetexId());

            String entityTypeName = key(identifiedEntity);

            gaplessIdGenerator.getNextIdForEntity(entityTypeName, claimedId);
        }
    }

    private String key(IdentifiedEntity identifiedEntity) {
        return identifiedEntity.getClass().getSimpleName();
    }
}
