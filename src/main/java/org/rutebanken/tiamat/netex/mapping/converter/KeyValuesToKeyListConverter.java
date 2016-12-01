package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.Value;

import java.util.Map;

public class KeyValuesToKeyListConverter extends CustomConverter<Map<String, Value>, KeyListStructure> {
    @Override
    public KeyListStructure convert(Map<String, Value> stringValueMap, Type<? extends KeyListStructure> type) {
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
