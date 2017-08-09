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
