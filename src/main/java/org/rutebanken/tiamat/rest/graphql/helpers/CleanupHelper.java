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

package org.rutebanken.tiamat.rest.graphql.helpers;

import java.util.*;

/**
 * Trimming whitespace on all input-fields of type GraphQLString
 */
public class CleanupHelper {

    public static void trimValues(Map<String, Object> arguments) {
        Set<String> keys = arguments.keySet();
        for (String key : keys) {
            Object o = arguments.get(key);
            if (o instanceof String) {
                arguments.put(key, ((String) o).trim());
            } else if (o instanceof List) {
                List trimmedList = new ArrayList();
                for (Object value : (List)o) {

                    if (value instanceof String) {
                        value = ((String)value).trim();
                    } else if (value instanceof HashMap) {
                        trimValues((Map<String, Object>) value);
                    }

                    if (value != null && !value.equals("")) {
                        trimmedList.add(value);
                    }
                }
                arguments.put(key, trimmedList);
            } else if (o instanceof HashMap) {
                trimValues((Map<String, Object>) o);
            }
        }
    }
}
