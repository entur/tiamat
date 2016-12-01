package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.Value;

import java.util.Map;

public class KeyListToKeyValuesMapMapper extends CustomMapper<KeyListStructure, Map<String, Value>> {
    @Override
    public void mapAtoB(KeyListStructure keyListStructure, Map<String, Value> stringValueMap, MappingContext context) {
        if(keyListStructure != null && keyListStructure.getKeyValue() != null && !keyListStructure.getKeyValue().isEmpty()) {
            for(KeyValueStructure keyValueStructure : keyListStructure.getKeyValue()) {
                stringValueMap.put(keyValueStructure.getKey(), new Value(keyValueStructure.getValue()));
            }
        }
    }
}
