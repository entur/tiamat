package org.rutebanken.tiamat.netexmapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.Value;

import java.util.Map;

public class KeyListMapper extends CustomMapper<KeyListStructure, Map<String, Value>> {
    @Override
    public void mapAtoB(KeyListStructure keyListStructure, Map<String, Value> stringValueMap, MappingContext context) {
        if(keyListStructure != null && keyListStructure.getKeyValue() != null && !keyListStructure.getKeyValue().isEmpty()) {
            for(KeyValueStructure keyValueStructure : keyListStructure.getKeyValue()) {
                stringValueMap.put(keyValueStructure.getKey(), new Value(keyValueStructure.getValue()));
            }
        }
    }

    @Override
    public void mapBtoA(Map<String, Value> stringValueMap, KeyListStructure keyListStructure, MappingContext context) {

        if(stringValueMap != null) {
            for (String key : stringValueMap.keySet()) {
                String value = String.join(",", stringValueMap.get(key).getItems());
                keyListStructure.getKeyValue().add(new KeyValueStructure().withKey(key).withValue(value));
            }
        }
    }
}
