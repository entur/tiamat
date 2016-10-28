package org.rutebanken.tiamat.netexmapping.converters;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyValueConverter extends BidirectionalConverter<KeyListStructure, Map<String, Value>> {


    @Override
    public Map<String, Value> convertTo(KeyListStructure keyListStructure, Type<Map<String, Value>> type) {
        Map<String, Value> values = new HashMap<>();
        if(keyListStructure != null && keyListStructure.getKeyValue() != null && !keyListStructure.getKeyValue().isEmpty()) {
            for(KeyValueStructure keyValueStructure : keyListStructure.getKeyValue()) {
                values.put(keyValueStructure.getKey(), new Value(keyValueStructure.getValue()));
            }
        }
        return values;
    }

    @Override
    public KeyListStructure convertFrom(Map<String, Value> stringListMap, Type<KeyListStructure> type) {
        KeyListStructure keyListStructure = new KeyListStructure().withKeyValue(new ArrayList<>());
        if(stringListMap != null) {
            for(String key : stringListMap.keySet()) {
                String value = String.join(",", stringListMap.get(key).getItems());
                keyListStructure.getKeyValue().add(new KeyValueStructure().withKey(key).withValue(value));
            }
        }
        return keyListStructure;
    }
}
