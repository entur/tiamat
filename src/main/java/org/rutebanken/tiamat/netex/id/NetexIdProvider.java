package org.rutebanken.tiamat.netex.id;

import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NetexIdProvider {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdProvider.class);


    private final GaplessIdGeneratorService gaplessIdGenerator;

    private final List<String> validPrefixForClaiming;

    @Autowired
    public NetexIdProvider(GaplessIdGeneratorService gaplessIdGenerator,
                           @Value("${netex.id.valid.prefix.list:NSR,KVE}") List<String> validPrefixForClaiming) {
        this.gaplessIdGenerator = gaplessIdGenerator;
        this.validPrefixForClaiming = validPrefixForClaiming;

        logger.info("Valid prefixes for claiming explicit IDs: {}", validPrefixForClaiming);
    }

    public String getGeneratedId(IdentifiedEntity identifiedEntity) throws InterruptedException {
        String entityTypeName = key(identifiedEntity);

        long longId = gaplessIdGenerator.getNextIdForEntity(entityTypeName);

        return NetexIdHelper.getNetexId(entityTypeName, String.valueOf(longId));
    }

    public void claimId(IdentifiedEntity identifiedEntity) {

        String prefix = NetexIdHelper.extractIdPrefix(identifiedEntity.getNetexId());

        if (!validPrefixForClaiming.contains(prefix)) {
            logger.warn("Detected non NSR ID: {} with prefix {}", identifiedEntity.getNetexId(), prefix);
        } else {
            logger.debug("Claimed ID contains valid prefix for claiming: {}", prefix);

            Long claimedId = NetexIdHelper.extractIdPostfix(identifiedEntity.getNetexId());

            String entityTypeName = key(identifiedEntity);

            gaplessIdGenerator.getNextIdForEntity(entityTypeName, claimedId);
        }
    }

    private String key(IdentifiedEntity identifiedEntity) {
        return identifiedEntity.getClass().getSimpleName();
    }

}
