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

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
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
