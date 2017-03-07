package org.rutebanken.tiamat.netex.mapping.mapper;

import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Random;

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
        if(internalEntity.getNetexId() == null) {
            logger.warn("Netex ID for internal model object is null. Mapping to null value. Object: {}", internalEntity);
            netexEntity.setId(null);
        } else {
            netexEntity.setId(internalEntity.getNetexId());
        }
    }

    public void toTiamatModel(org.rutebanken.netex.model.DataManagedObjectStructure netexEntity, DataManagedObjectStructure tiamatEntity) {

        if(netexEntity.getId() == null) {
            tiamatEntity.setNetexId(null);
        } else if(netexEntity.getId().startsWith(NSR)) {
            logger.debug("Detected tiamat ID: {}. ", netexEntity.getId());
            tiamatEntity.setNetexId(netexEntity.getId());
        } else {
            logger.debug("Received ID {}. Will save it as key value ", netexEntity.getId());
            moveOriginalIdToKeyValueList(tiamatEntity, netexEntity.getId());
            tiamatEntity.setNetexId(null);
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
    public static long getNetexIdPostfix(String netexId) {
        try {
            return Long.valueOf(netexId.substring(netexId.lastIndexOf(':') + 1));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Cannot parse NeTEx ID into internal ID: '" + netexId +"'");
        }
    }

    public static String getNetexId(String type, String id) {
        return NSR + ":" + type + ":" + id;
    }

    public static String generateNetexId(IdentifiedEntity identifiedEntity) {
        return getNetexId(determineIdType(identifiedEntity), String.valueOf(new Random().nextInt()));
    }

    public static boolean isNsrId(String netexId) {
        return netexId.contains(NetexIdMapper.NSR);
    }

    public static Optional<String> getOptionalTiamatId(String netexId) {
        if (isNsrId(netexId)) {
            logger.debug("Detected tiamat ID from {}", netexId);
            return Optional.of(netexId);
        } else {
            return Optional.empty();
        }
    }

    private static String determineIdType(IdentifiedEntity identifiedEntity) {

        if(identifiedEntity instanceof StopPlace) {
            return "StopPlace";
        } else if (identifiedEntity instanceof Quay){
            return "Quay";
        } else if (identifiedEntity instanceof SiteFrame) {
            return "SiteFrame";
        } else if (identifiedEntity instanceof PathLinkEnd) {
            return "PathLinkEnd";
        } else {
            return identifiedEntity.getClass().getSimpleName();
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
