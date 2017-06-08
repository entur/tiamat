package org.rutebanken.tiamat.rest.graphql.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.rest.graphql.helpers.KeyValueWrapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class KeyValuesDataFetcher implements DataFetcher {

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
        DataManagedObjectStructure source = (DataManagedObjectStructure) dataFetchingEnvironment.getSource();
        if (source != null) {
            List<KeyValueWrapper> keyValuesList = new ArrayList();

            Map<String, Value> keyValues = source.getKeyValues();

            keyValues.keySet().forEach(key -> {
                keyValuesList.add(new KeyValueWrapper(key, keyValues.get(key)));
            });

            return keyValuesList;
        } else {
            return null;
        }
    }
}
