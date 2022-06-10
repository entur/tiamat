/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.netex.mapping.mapper;

import com.google.common.base.Strings;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.rutebanken.tiamat.netex.mapping.mapper.DataManagedObjectStructureMapper.CHANGED_BY;
import static org.rutebanken.tiamat.netex.mapping.mapper.DataManagedObjectStructureMapper.VERSION_COMMENT;
import static org.rutebanken.tiamat.netex.mapping.mapper.StopPlaceMapper.IS_PARENT_STOP_PLACE;

/**
 * Note: Implemented because of an issue with using
 * CustomMapper<EntityStructure, org.rutebanken.tiamat.model.EntityStructure>
 * and missing default mapping for subtypes
 **/
@Component
public class NetexIdMapper {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdMapper.class);

    public static final String ORIGINAL_ID_KEY = "imported-id";
    public static final String MERGED_ID_KEY = "merged-id";

    private static final List<String> IGNORE_KEYS = Arrays.asList(CHANGED_BY, VERSION_COMMENT, IS_PARENT_STOP_PLACE);

    @Autowired
    private final ValidPrefixList validPrefixList;

    @Autowired
    private final NetexIdHelper netexIdHelper;

    public NetexIdMapper(ValidPrefixList validPrefixList, NetexIdHelper netexIdHelper) {
        this.validPrefixList = validPrefixList;
        this.netexIdHelper = netexIdHelper;
    }

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
        } else if(validPrefixList.isValidPrefixForType(netexIdHelper.extractIdPrefix(netexEntity.getId()), tiamatEntity.getClass())) {
            logger.debug("Detected ID with valid prefix: {}. ", netexEntity.getId());
            tiamatEntity.setNetexId(netexEntity.getId().trim());
        } else {
            logger.debug("Received ID {}. Will map it as key value ", netexEntity.getId());
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
                    String value = keyValueStructure.getValue();
                    String key = keyValueStructure.getKey();

                    if(IGNORE_KEYS.contains(key)) {
                        // Mapped elsewhere
                        continue;
                    }

                    boolean ignoreEmptyPostfix = (key.equals(ORIGINAL_ID_KEY) | key.equals(MERGED_ID_KEY));

                    if (value.contains(",")) {
                        String[] originalIds = value.split(",");
                        for (String originalId : originalIds) {
                            addKeyValueAvoidEmpty(tiamatEntity, key, originalId, ignoreEmptyPostfix);
                        }
                    } else {
                        addKeyValueAvoidEmpty(tiamatEntity, key, value, ignoreEmptyPostfix);
                    }
                }
            }
        }
    }

    private void addKeyValueAvoidEmpty(DataManagedObjectStructure tiamatEntity, final String key, final String value, boolean ignoreEmptyPostfix) {

        String keytoAdd = key.trim();
        String valueToAdd = value.trim();

        if(ignoreEmptyPostfix) {
            if(Strings.isNullOrEmpty(netexIdHelper.extractIdPostfix(valueToAdd))) {
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
