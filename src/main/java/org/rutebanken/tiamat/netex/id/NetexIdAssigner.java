package org.rutebanken.tiamat.netex.id;

import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Assign Netex ID to IdentifiedObject.
 * Does not generate the ID.
 */
@Component("netexIdAssigner")
public class NetexIdAssigner {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdAssigner.class);

    private NetexIdProvider netexIdProvider;

    @Autowired
    public NetexIdAssigner(NetexIdProvider netexIdProvider) {
        this.netexIdProvider = netexIdProvider;
    }

    public void assignNetexId(IdentifiedEntity identifiedEntity) {

        if(identifiedEntity.getNetexId() == null) {
            logger.debug("No ID set on {}", identifiedEntity);
            try {
                String netexId = netexIdProvider.getGeneratedId(identifiedEntity);
                identifiedEntity.setNetexId(netexId);
                logger.debug("Assigned ID {} to entity", netexId);
            } catch (InterruptedException e) {
                throw new RuntimeException("Error generating new ID for entity: "+identifiedEntity, e);
            }

        } else {
            logger.debug("Incoming object claims explicit netex ID {}.", identifiedEntity.getNetexId());

            netexIdProvider.claimId(identifiedEntity);
        }
    }


}
