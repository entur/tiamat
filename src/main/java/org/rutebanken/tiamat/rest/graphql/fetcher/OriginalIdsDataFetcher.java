package org.rutebanken.tiamat.rest.graphql.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.springframework.stereotype.Component;

@Component
public class OriginalIdsDataFetcher implements DataFetcher {

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
        DataManagedObjectStructure source = (DataManagedObjectStructure) dataFetchingEnvironment.getSource();
        if (source != null) {
            return source.getOriginalIds();
        } else {
            return null;
        }
    }
}
