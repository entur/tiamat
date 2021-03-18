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

package org.rutebanken.tiamat.rest.graphql.mappers;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.KEY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.KEY_VALUES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALUES;

import java.util.List;
import java.util.Map;

import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.Value;

/**
 * This class maps key values onto a DataManagedObjectStructure object.
 *
 * @author Dick Zetterberg (dick@transitor.se)
 * @version 2020-06-01
 */
public class KeyValuesMapper {

    public static boolean populate(Map input, DataManagedObjectStructure entity) {
        if (input.get(KEY_VALUES) == null) {
            return false;
        }
        List<Map> keyValues = (List) input.get(KEY_VALUES);
        entity.getKeyValues().clear();
        for (Map inputMap : keyValues) {
            String key = (String) inputMap.get(KEY);
            List<String> values = (List<String>) inputMap.get(VALUES);

            Value value = new Value(values);
            entity.getKeyValues().put(key, value);
        }
        return true;
    }
}