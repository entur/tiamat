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

package org.rutebanken.tiamat.service.merge;

import org.rutebanken.tiamat.model.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class KeyValuesMerger {

    public void mergeKeyValues(Map<String, Value> fromKeyValues, Map<String, Value> toKeyValues) {
        fromKeyValues.keySet()
                .forEach(key -> {
                    if (toKeyValues.containsKey(key)) {
                        toKeyValues.get(key).getItems().addAll(fromKeyValues.get(key).getItems());
                    } else {
                        Value value = fromKeyValues.get(key);

                        List<String> valueItems = new ArrayList<>();
                        valueItems.addAll(value.getItems());

                        toKeyValues.put(key, new Value(valueItems));
                    }
                });
    }

}
