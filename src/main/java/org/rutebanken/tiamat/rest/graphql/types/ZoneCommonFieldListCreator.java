package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.rest.graphql.fetcher.OriginalIdsDataFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.*;

@Component
public class ZoneCommonFieldListCreator {

    private static final Logger logger = LoggerFactory.getLogger(ZoneCommonFieldListCreator.class);

    @Autowired
    private OriginalIdsDataFetcher originalIdsDataFetcher;

    public List<GraphQLFieldDefinition> create() {

        List<GraphQLFieldDefinition> zoneFieldList = new ArrayList<>();
        zoneFieldList.add(netexIdFieldDefinition);
        zoneFieldList.add(newFieldDefinition().name(NAME).type(embeddableMultilingualStringObjectType).build());
        zoneFieldList.add(newFieldDefinition().name(SHORT_NAME).type(embeddableMultilingualStringObjectType).build());
        zoneFieldList.add(newFieldDefinition().name(DESCRIPTION).type(embeddableMultilingualStringObjectType).build());
        zoneFieldList.add(newFieldDefinition().name(VERSION).type(GraphQLString).build());
        zoneFieldList.add(geometryFieldDefinition);

        zoneFieldList.add(newFieldDefinition()
                .name(IMPORTED_ID)
                .type(new GraphQLList(GraphQLString))
                .dataFetcher(originalIdsDataFetcher)
                .build());

        return zoneFieldList;
    }
}
