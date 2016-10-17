package org.rutebanken.tiamat.netexmapping;

import org.rutebanken.tiamat.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**Note: Implemented because of an issue with using
 * CustomMapper<EntityStructure, org.rutebanken.tiamat.model.EntityStructure>
 * and missing default mapping for subtypes
 **/
public class NetexIdMapper {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdMapper.class);

    public static final String ORIGINAL_ID_KEY = "imported-id";

    // TODO: make it configurable
    public static final String NSR = "NSR";

    public void toNetexModel(DataManagedObjectStructure internalEntity, org.rutebanken.netex.model.DataManagedObjectStructure netexEntity) {
        if(internalEntity.getId() == null) {
            logger.warn("Id for internal model is null. Mapping to null value.");
            netexEntity.setId(null);
        } else {
            netexEntity.setId("NSR:" +  determineIdType(internalEntity)+":" + internalEntity.getId().toString());
        }
    }

    public void toTiamatModel(org.rutebanken.netex.model.DataManagedObjectStructure netexEntity, DataManagedObjectStructure tiamatEntity) {

        if(netexEntity.getId() == null) {
            tiamatEntity.setId(null);
        } else if(netexEntity.getId().startsWith(NSR)) {
            logger.debug("Detected tiamat ID: {} ", netexEntity.getId());
            String netexId = netexEntity.getId();
            Long tiamatId = Long.valueOf(netexId.substring(netexId.lastIndexOf(':') + 1));
            tiamatEntity.setId(tiamatId);
        } else {
            logger.debug("Received ID {}. Will save it as key value ", netexEntity.getId());
            moveOriginalIdToKeyValue(tiamatEntity, netexEntity.getId());
            tiamatEntity.setId(null);
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

    public void moveOriginalIdToKeyValue(DataManagedObjectStructure dataManagedObjectStructure, String netexId) {
        KeyValueStructure originalId = new KeyValueStructure();
        originalId.setKey(ORIGINAL_ID_KEY);
        originalId.setValue(netexId);
        if (dataManagedObjectStructure.getKeyList() == null) {
            dataManagedObjectStructure.setKeyList(new KeyListStructure());
        }
        dataManagedObjectStructure.getKeyList().getKeyValue().add(originalId);

        logger.debug("Moved ID {} to key {} when mapping to internal model", originalId.getValue(), ORIGINAL_ID_KEY);
    }
}
