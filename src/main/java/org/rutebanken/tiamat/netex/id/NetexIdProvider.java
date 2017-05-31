package org.rutebanken.tiamat.netex.id;

import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NetexIdProvider {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdProvider.class);

    private final GaplessIdGeneratorService gaplessIdGenerator;

    private final ValidPrefixList validPrefixList;

    @Autowired
    public NetexIdProvider(GaplessIdGeneratorService gaplessIdGenerator, ValidPrefixList validPrefixList) {
        this.gaplessIdGenerator = gaplessIdGenerator;
        this.validPrefixList = validPrefixList;


    }

    public String getGeneratedId(IdentifiedEntity identifiedEntity) throws InterruptedException {
        String entityTypeName = key(identifiedEntity);

        long longId = gaplessIdGenerator.getNextIdForEntity(entityTypeName);

        return NetexIdHelper.getNetexId(entityTypeName, longId);
    }

    public void claimId(IdentifiedEntity identifiedEntity) {

        String prefix = NetexIdHelper.extractIdPrefix(identifiedEntity.getNetexId());

        if(validPrefixList.isValidPrefixForType(prefix, identifiedEntity.getClass())) {
            logger.debug("Claimed ID contains valid prefix for claiming: {}", prefix);

            if(NetexIdHelper.isNsrId(identifiedEntity.getNetexId())) {
                Long claimedId = NetexIdHelper.extractIdPostfixNumeric(identifiedEntity.getNetexId());

                String entityTypeName = key(identifiedEntity);

                gaplessIdGenerator.getNextIdForEntity(entityTypeName, claimedId);
            }
            // Because IDs might end with non-numbers we cannot support claiming for any ID other than NSR.
        } else {
            logger.warn("Detected non NSR ID: {} with prefix {}", identifiedEntity.getNetexId(), prefix);
        }
    }

    private String key(IdentifiedEntity identifiedEntity) {
        return identifiedEntity.getClass().getSimpleName();
    }

}
