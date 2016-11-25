package org.rutebanken.tiamat.netexmapping;

import org.rutebanken.tiamat.importers.KeyValueListAppender;
import org.rutebanken.tiamat.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note: Implemented because of an issue with using
 * CustomMapper<EntityStructure, org.rutebanken.tiamat.model.EntityStructure>
 * and missing default mapping for subtypes
 **/
public class NetexIdMapper {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdMapper.class);

    public static final String ORIGINAL_ID_KEY = "imported-id";

    private KeyValueListAppender keyValueListAppender = new KeyValueListAppender();

    // TODO: make it configurable
    public static final String NSR = "NSR";

    public void toNetexModel(EntityStructure internalEntity, org.rutebanken.netex.model.EntityStructure netexEntity) {
        if(internalEntity.getId() == null) {
            logger.warn("Id for internal model is null. Mapping to null value. Object: {}", internalEntity);
            netexEntity.setId(null);
        } else {
            netexEntity.setId(getNetexId(determineIdType(internalEntity), internalEntity.getId().toString()));
        }
    }

    public static String getNetexId(String idType, String id) {
        return "NSR:" + idType +":" + id;
    }

    public void toTiamatModel(org.rutebanken.netex.model.DataManagedObjectStructure netexEntity, DataManagedObjectStructure tiamatEntity) {

        if(netexEntity.getId() == null) {
            tiamatEntity.setId(null);
        }/* else if(netexEntity.getId().startsWith(NSR)) {
            logger.debug("Detected tiamat ID: {} ", netexEntity.getId());
            String netexId = netexEntity.getId();
            Long tiamatId = Long.valueOf(netexId.substring(netexId.lastIndexOf(':') + 1));
            tiamatEntity.setId(tiamatId);
        } */else {
            logger.debug("Received ID {}. Will save it as key value ", netexEntity.getId());
            moveOriginalIdToKeyValueList(tiamatEntity, netexEntity.getId());
            tiamatEntity.setId(null);
        }
    }

    private String determineIdType(EntityStructure entityStructure) {

        if(entityStructure instanceof StopPlace) {
            return "StopPlace";
        } else if (entityStructure instanceof Quay){
            return "Quay";
        } else if (entityStructure instanceof SiteFrame){
            return "SiteFrame";
        } else {
            return entityStructure.getClass().getSimpleName();
        }
    }

    public void moveOriginalIdToKeyValueList(DataManagedObjectStructure dataManagedObjectStructure, String netexId) {
        dataManagedObjectStructure.getKeyValues().put(ORIGINAL_ID_KEY, new Value(netexId));
    }

}
