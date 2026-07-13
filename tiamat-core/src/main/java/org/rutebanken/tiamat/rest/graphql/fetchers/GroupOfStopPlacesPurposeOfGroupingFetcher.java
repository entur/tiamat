package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.service.groupofstopplaces.GroupOfStopPlacesPurposeOfGroupingResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupOfStopPlacesPurposeOfGroupingFetcher  implements DataFetcher<PurposeOfGrouping> {
    @Autowired
    private GroupOfStopPlacesPurposeOfGroupingResolver groupOfStopPlacesPurposeOfGroupingResolver;
    @Override
    public PurposeOfGrouping get(DataFetchingEnvironment environment) throws Exception {
        GroupOfStopPlaces groupOfStopPlaces = environment.getSource();
        return groupOfStopPlacesPurposeOfGroupingResolver.resolve(groupOfStopPlaces);
    }
}
