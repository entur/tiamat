package org.rutebanken.tiamat.netex.id;

import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("netexIdAssigner")
public class NetexIdAssigner {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdAssigner.class);

    public void assignNetexId(IdentifiedEntity identifiedEntity) {

        if(identifiedEntity.getNetexId() == null) {
            logger.info("No ID set on {}", identifiedEntity);
            String netexId = NetexIdMapper.generateNetexId(identifiedEntity);
            identifiedEntity.setNetexId(netexId);
        } else {
            logger.info("Object does already have ID set: {}", identifiedEntity);
        }
    }


}
