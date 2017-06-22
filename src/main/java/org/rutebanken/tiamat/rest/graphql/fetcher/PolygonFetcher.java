package org.rutebanken.tiamat.rest.graphql.fetcher;


import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.springframework.stereotype.Component;

@Component
public class PolygonFetcher implements DataFetcher {

    @Override
    public Object get(DataFetchingEnvironment environment) {
        if(environment.getSource() instanceof Zone_VersionStructure) {
            return ((Zone_VersionStructure) environment.getSource()).getPolygon();
        }
        return null;
    }
}
