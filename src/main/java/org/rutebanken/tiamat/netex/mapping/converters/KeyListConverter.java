package org.rutebanken.tiamat.netex.mapping.converters;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.Value;

import java.util.HashMap;
import java.util.Map;

public class KeyListConverter extends BidirectionalConverter<KeyListStructure, Map<String, Value>> {
    @Override
    public Map<String, Value> convertTo(KeyListStructure keyListStructure, Type<Map<String, Value>> type) {
        if(keyListStructure == null) {
            return new HashMap<>();
        } else {
            Map<String, Value> keyValues = new HashMap<>();
            if(keyListStructure != null && keyListStructure.getKeyValue() != null && !keyListStructure.getKeyValue().isEmpty()) {
                for(KeyValueStructure keyValueStructure : keyListStructure.getKeyValue()) {
                    keyValues.put(keyValueStructure.getKey(), new Value(keyValueStructure.getValue()));
                }
            }
            return keyValues;
        }
    }

    @Override
    public KeyListStructure convertFrom(Map<String, Value> stringValueMap, Type<KeyListStructure> type) {
        if(stringValueMap != null && !stringValueMap.isEmpty()) {
            KeyListStructure keyListStructure = new KeyListStructure();
            for (String key : stringValueMap.keySet()) {
                String value = String.join(",", stringValueMap.get(key).getItems());
                keyListStructure.getKeyValue().add(new KeyValueStructure().withKey(key).withValue(value));
            }
            return keyListStructure;
        }
        return null;
    }
}
