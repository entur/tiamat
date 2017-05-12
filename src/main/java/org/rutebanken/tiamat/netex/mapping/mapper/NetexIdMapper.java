package org.rutebanken.tiamat.netex.mapping.mapper;

import com.google.common.base.Strings;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;

import static org.rutebanken.tiamat.netex.id.NetexIdHelper.stripLeadingZeros;

/**
 * Note: Implemented because of an issue with using
 * CustomMapper<EntityStructure, org.rutebanken.tiamat.model.EntityStructure>
 * and missing default mapping for subtypes
 **/
@Component
public class NetexIdMapper {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdMapper.class);

    public static final String ORIGINAL_ID_KEY = "imported-id";

    @Autowired
    private ValidPrefixList validPrefixList;

    public void toNetexModel(EntityStructure internalEntity, org.rutebanken.netex.model.EntityStructure netexEntity) {
        if(internalEntity.getNetexId() == null) {
            logger.warn("Netex ID for internal model object is null. Mapping to null value. Object: {}", internalEntity);
            netexEntity.setId(null);
        } else {
            netexEntity.setId(internalEntity.getNetexId());
        }
    }

    public void toTiamatModel(org.rutebanken.netex.model.EntityInVersionStructure netexEntity, EntityInVersionStructure tiamatEntity) {

        if(netexEntity.getId() == null) {
            tiamatEntity.setNetexId(null);
        } else if(validPrefixList.isValidPrefixForType(NetexIdHelper.extractIdPrefix(netexEntity.getId()), tiamatEntity.getClass())) {
            logger.debug("Detected ID with valid prefix: {}. ", netexEntity.getId());
            tiamatEntity.setNetexId(netexEntity.getId().trim());
        } else {
            logger.debug("Received ID {}. Will save it as key value ", netexEntity.getId());
            if(tiamatEntity instanceof  DataManagedObjectStructure) {
                moveOriginalIdToKeyValueList((DataManagedObjectStructure) tiamatEntity, netexEntity.getId());
                tiamatEntity.setNetexId(null);
            }
        }
        if(netexEntity instanceof org.rutebanken.netex.model.DataManagedObjectStructure && tiamatEntity instanceof DataManagedObjectStructure) {
            logger.debug("Copy key values to tiamat model {}", tiamatEntity.getNetexId());
            copyKeyValuesToTiamatModel((org.rutebanken.netex.model.DataManagedObjectStructure) netexEntity, (DataManagedObjectStructure) tiamatEntity);
        }
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
                                addKeyValueAvoidEmpty(tiamatEntity, ORIGINAL_ID_KEY, originalId, true);
                            }
                        } else {
                            addKeyValueAvoidEmpty(tiamatEntity, ORIGINAL_ID_KEY, keyValueStructure.getValue(), true);
                        }

                    } else {
                        addKeyValueAvoidEmpty(tiamatEntity, keyValueStructure.getKey(), keyValueStructure.getValue(), false);
                    }
                }
            }
        }
    }

    private void addKeyValueAvoidEmpty(DataManagedObjectStructure tiamatEntity, final String key, final String value, boolean ignoreEmptyPostfix) {

        String keytoAdd = key.trim();
        String valueToAdd = value.trim();

        if(ignoreEmptyPostfix) {
            if(Strings.isNullOrEmpty(NetexIdHelper.extractIdPostfix(valueToAdd))) {
                logger.debug("Ignoring empty postfix for key value: key {} and value '{}'", keytoAdd, valueToAdd);
                return;
            }
        }


        if(!Strings.isNullOrEmpty(keytoAdd) && !Strings.isNullOrEmpty(valueToAdd)) {
            logger.trace("Adding key {} and value {}", keytoAdd, valueToAdd);
            tiamatEntity.getOrCreateValues(keytoAdd).add(valueToAdd);
        }
    }

    /**
     * Writes netex ID to keyval in internal Tiamat model
     * @param dataManagedObjectStructure to set the keyval on (tiamat model)
     * @param netexId The id to add to values, using the key #{ORIGINAL_ID_KEY}
     */
    public void moveOriginalIdToKeyValueList(DataManagedObjectStructure dataManagedObjectStructure, String netexId) {
        addKeyValueAvoidEmpty(dataManagedObjectStructure, ORIGINAL_ID_KEY, netexId, true);
    }

}
