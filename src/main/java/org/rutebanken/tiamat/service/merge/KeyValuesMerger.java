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
