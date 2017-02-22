package org.rutebanken.tiamat.netex.mapping.mapper;

import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Note: Implemented because of an issue with using
 * CustomMapper<EntityStructure, org.rutebanken.tiamat.model.EntityStructure>
 * and missing default mapping for subtypes
 **/
@Component
public class NetexIdMapper {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdMapper.class);

    public static final String ORIGINAL_ID_KEY = "imported-id";

    // TODO: make it configurable
    public static final String NSR = "NSR";

    public void toNetexModel(EntityStructure internalEntity, org.rutebanken.netex.model.EntityStructure netexEntity) {
        if(internalEntity.getId() == null) {
            logger.warn("Id for internal model is null. Mapping to null value. Object: {}", internalEntity);
            netexEntity.setId(null);
        } else {
            netexEntity.setId(getNetexId(internalEntity));
        }
    }

    public static String getNetexId(EntityStructure internalEntity, Long id) {
        return getNetexId(determineIdType(internalEntity), String.valueOf(id));
    }

    public static String getNetexId(EntityStructure internalEntity) {
        return getNetexId(internalEntity, internalEntity.getId());
    }

    public static String getNetexId(PathLinkEnd pathLinkEnd) {
        return getNetexId("PathLinkEnd", String.valueOf(pathLinkEnd.getId()));
    }

    public static String getNetexId(String type, String id) {
        return NSR + ":" + type + ":" + id;
    }

    public void toTiamatModel(org.rutebanken.netex.model.DataManagedObjectStructure netexEntity, DataManagedObjectStructure tiamatEntity) {

        if(netexEntity.getId() == null) {
            tiamatEntity.setId(null);
        } else if(netexEntity.getId().startsWith(NSR)) {
            logger.debug("Detected tiamat ID: {}. ", netexEntity.getId());
            String netexId = netexEntity.getId();
            Long tiamatId = getTiamatId(netexId);
            tiamatEntity.setId(tiamatId);
        } else {
            logger.debug("Received ID {}. Will save it as key value ", netexEntity.getId());
            moveOriginalIdToKeyValueList(tiamatEntity, netexEntity.getId());
            tiamatEntity.setId(null);
        }
        logger.debug("Copy key values to tiamat model");
        copyKeyValuesToTiamatModel(netexEntity, tiamatEntity);
    }

    /**
     * Copies key values from netex object to internal Tiamat model.
     * The internal Tiamat model can hold lists of values for each key.
     * Therefore, if the key matches ORIGINAL_ID_KEY, the incoming values will be separated by comma.
     * @param netexEntity netexEntity containing key values. If it contains ORIGINAL_ID_KEY. Values will be separated.
     * @param tiamatEntity tiamat entity to add key values to.
     */
    public void copyKeyValuesToTiamatModel(org.rutebanken.netex.model.DataManagedObjectStructure netexEntity, DataManagedObjectStructure tiamatEntity) {
        if(netexEntity.getKeyList() != null) {
            if(netexEntity.getKeyList().getKeyValue() != null) {
                for(KeyValueStructure keyValueStructure : netexEntity.getKeyList().getKeyValue()) {
                    if(keyValueStructure.getKey().equals(ORIGINAL_ID_KEY)) {
                        if(keyValueStructure.getValue().contains(",")) {
                            String[] originalIds = keyValueStructure.getValue().split(",");
                            for(String originalId : originalIds) {
                                tiamatEntity.getOrCreateValues(ORIGINAL_ID_KEY).add(originalId);
                            }
                        } else {
                            tiamatEntity.getOrCreateValues(ORIGINAL_ID_KEY).add(keyValueStructure.getValue());
                        }

                    } else {
                        tiamatEntity.getOrCreateValues(keyValueStructure.getKey()).add(keyValueStructure.getValue());
                    }
                }
            }
        }
    }

    /**
     *
     * @param netexId Id with long value after last colon.
     * @return long value
     */
    public static long getTiamatId(String netexId) {
        try {
            return Long.valueOf(netexId.substring(netexId.lastIndexOf(':') + 1));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Cannot parse NeTEx ID into internal ID: '" + netexId +"'");
        }

    }

    private static boolean isInternalTiamatId(String netexId) {
        return netexId.contains(NetexIdMapper.NSR);
    }

    public static Optional<Long> getOptionalTiamatId(String netexId) {
        if (isInternalTiamatId(netexId)) {
            logger.debug("Detected tiamat ID from {}", netexId);
            return Optional.of(getTiamatId(netexId));
        } else {
            return Optional.empty();
        }
    }

    private static String determineIdType(EntityStructure entityStructure) {

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

    /**
     * Writes netex ID to keyval in internal Tiamat model
     * @param dataManagedObjectStructure to set the keyval on (tiamat model)
     * @param netexId The id to add to values, using the key #{ORIGINAL_ID_KEY}
     */
    public void moveOriginalIdToKeyValueList(DataManagedObjectStructure dataManagedObjectStructure, String netexId) {
        dataManagedObjectStructure.getOrCreateValues(ORIGINAL_ID_KEY).add(netexId);
    }

}
