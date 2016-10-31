package org.rutebanken.tiamat.netexmapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.hibernate.collection.internal.PersistentMap;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.Value;

import java.util.List;
import java.util.Map;

/**
 * Implemented because of difficulties making orika use the KeyListMapper.
 */
public class KeyListPersistentBagMapper extends CustomMapper<KeyListStructure, PersistentMap> {
    @Override
    public void mapAtoB(KeyListStructure keyListStructure, PersistentMap stringValueMap, MappingContext context) {
        if(keyListStructure != null && keyListStructure.getKeyValue() != null && !keyListStructure.getKeyValue().isEmpty()) {
            for(KeyValueStructure keyValueStructure : keyListStructure.getKeyValue()) {
                stringValueMap.put(keyValueStructure.getKey(), new Value(keyValueStructure.getValue()));
            }
        }
    }

    @Override
    public void mapBtoA(PersistentMap stringValueMap, KeyListStructure keyListStructure, MappingContext context) {

        if(stringValueMap != null) {
            for (Object key : stringValueMap.keySet()) {
                String stringKey = (String) key;
                Value value = (Value) stringValueMap.get(key);
                String commaSeparatedString = String.join(",", value.getItems());
                keyListStructure.getKeyValue().add(new KeyValueStructure().withKey(stringKey).withValue(commaSeparatedString));
            }
        }
    }
}
