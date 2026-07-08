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

package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KeyValuesToKeyListConverter extends CustomConverter<Map<String, Value>, KeyListStructure> {
    @Override
    public KeyListStructure convert(Map<String, Value> stringValueMap, Type<? extends KeyListStructure> type, MappingContext mappingContext) {
        if(stringValueMap != null) {
            KeyListStructure keyListStructure = new KeyListStructure();
            for (String key : stringValueMap.keySet()) {
                Value values = stringValueMap.get(key);
                if(values != null && values.getItems() != null) {
                    String value = String.join(",", values.getItems());
                    keyListStructure.getKeyValue().add(new KeyValueStructure().withKey(key).withValue(value));
                } else {
                    // No values
                    keyListStructure.getKeyValue().add(new KeyValueStructure().withKey(key));
                }
            }
            if(keyListStructure.getKeyValue().isEmpty()) {
                return null;
            }
            return keyListStructure;
        }
        return null;
    }
}
