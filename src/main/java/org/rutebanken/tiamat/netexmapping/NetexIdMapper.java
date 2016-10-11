package org.rutebanken.tiamat.netexmapping;

import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**Note: Implemented because of an issue with using
 * CustomMapper<EntityStructure, org.rutebanken.tiamat.model.EntityStructure>
 * and missing default mapping for subtypes
 **/
public class NetexIdMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetexIdMapper.class);

    public void toNetexModel(EntityStructure internalEntity, org.rutebanken.netex.model.EntityStructure netexEntity) {
        if(internalEntity.getId() == null) {
            LOGGER.warn("Id for internal model is null. Mapping to null value.");
            netexEntity.setId(null);
        } else {
            netexEntity.setId("NSR:" +  determineIdType(internalEntity)+":" + internalEntity.getId().toString());
        }
    }

    public void toTiamatModel(org.rutebanken.netex.model.EntityStructure netexEntity, EntityStructure tiamatEntity) {

        if(netexEntity.getId() == null) {
            tiamatEntity.setId(null);
        } else if (!netexEntity.getId().contains(":")) {
            throw new NumberFormatException("Id '" + netexEntity.getId()+"' not supported. Expected (At least) colon followed by Long value");
        } else {
            String netexId = netexEntity.getId();
            Long tiamatId = Long.valueOf(netexId.substring(netexId.lastIndexOf(':') + 1));
            tiamatEntity.setId(tiamatId);
        }
    }

    private String determineIdType(EntityStructure entityStructure) {

        if(entityStructure instanceof StopPlace) {
            return "StopPlace";
        } else if (entityStructure instanceof Quay){
            return "Quay";
        } else {
            return "Unknown";
        }

    }

}
