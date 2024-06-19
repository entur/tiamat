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

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Longs;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
public class DataManagedObjectStructureMapper extends CustomMapper<DataManagedObjectStructure, org.rutebanken.tiamat.model.DataManagedObjectStructure> {


    public static final String CHANGED_BY = "CHANGED_BY";
    public static final String VERSION_COMMENT = "VERSION_COMMENT";

    private final NetexIdMapper netexIdMapper;

    /**
     * Setters for internal tiamat model when mapping from netex.
     */
    private static final Map<String, BiConsumer<String, org.rutebanken.tiamat.model.DataManagedObjectStructure>> tiamatEntitySetFunctions = new ImmutableMap.Builder<String, BiConsumer<String, org.rutebanken.tiamat.model.DataManagedObjectStructure>>()
            .put(CHANGED_BY, (value, tiamatEntity) -> tiamatEntity.setChangedBy(value))
            .put(VERSION_COMMENT, (value, tiamatEntity) -> tiamatEntity.setVersionComment(value))
            .build();

    /**
     * Properties to map to key values in netex format. Getters for the tiamat entity.
     */
    private static final Map<String, Function<org.rutebanken.tiamat.model.DataManagedObjectStructure, String>> tiamatEntityGetFunctions = new ImmutableMap.Builder<String, Function<org.rutebanken.tiamat.model.DataManagedObjectStructure, String>>()
            .put(CHANGED_BY, org.rutebanken.tiamat.model.DataManagedObjectStructure::getChangedBy)
            .put(VERSION_COMMENT, org.rutebanken.tiamat.model.DataManagedObjectStructure::getVersionComment)
            .build();

    @Autowired
    public DataManagedObjectStructureMapper(NetexIdMapper netexIdMapper) {
        this.netexIdMapper = netexIdMapper;
    }

    @Override
    public void mapAtoB(DataManagedObjectStructure netexEntity, org.rutebanken.tiamat.model.DataManagedObjectStructure tiamatEntity, MappingContext context) {
        netexIdMapper.toTiamatModel(netexEntity, tiamatEntity);

        // Version is a field of superclass EntityInVersionStructure. Should strictly be mapped in an EntityInVersionStructureMapper or Converter.

        if (netexEntity.getVersion() != null) {
            if (netexEntity.getVersion().equals("any")) {
                tiamatEntity.setVersion(-1L); // Need to handle this value in import.
            } else {
                Long longVersion = Longs.tryParse(netexEntity.getVersion());
                if (longVersion != null) {
                    tiamatEntity.setVersion(longVersion);
                } else {
                    throw new NetexMappingException("Received version in netex format. " +
                            "But cannot parse version. Expecting a long value or the String 'any'. " +
                            "Value is: " + netexEntity.getVersion() + " Object: " + netexEntity);
                }
            }
        }

        if (netexEntity.getKeyList() != null && netexEntity.getKeyList().getKeyValue() != null) {
            netexEntity.getKeyList().getKeyValue().forEach(keyValueStructure -> {
                if (tiamatEntitySetFunctions.containsKey(keyValueStructure.getKey())) {
                    tiamatEntitySetFunctions.get(keyValueStructure.getKey()).accept(keyValueStructure.getValue(), tiamatEntity);
                    tiamatEntity.getKeyValues().remove(keyValueStructure.getValue());
                }
            });
        }
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.DataManagedObjectStructure tiamatEntity, DataManagedObjectStructure netexEntity, MappingContext context) {
        netexIdMapper.toNetexModel(tiamatEntity, netexEntity);
        netexEntity.setVersion(String.valueOf(tiamatEntity.getVersion()));

        if (netexEntity.getKeyList() == null) {
            netexEntity.withKeyList(new KeyListStructure());
        }
        tiamatEntityGetFunctions.forEach((property, function) -> setKey(netexEntity, property, function.apply(tiamatEntity)));

        if (netexEntity.getKeyList().getKeyValue() == null || netexEntity.getKeyList().getKeyValue().isEmpty()) {
            // Do not allow empty key list
            netexEntity.withKeyList(null);
        }
    }

    private void setKey(DataManagedObjectStructure netexEntity, String key, String value) {
        if (value == null) return;

        netexEntity.getKeyList()
                .withKeyValue(new KeyValueStructure()
                        .withKey(key)
                        .withValue(value));
    }
}

