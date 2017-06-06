package org.rutebanken.tiamat.rest.graphql.helpers;

import org.rutebanken.tiamat.model.Value;

import java.util.Set;

public class KeyValueWrapper {
    public String key;
    public Set<String> values;

    public KeyValueWrapper(String key, Value value) {
        this.key = key;
        if (value != null) {
            this.values = value.getItems();
        }
    }
}