package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class EntityRefObjectTypeCreator {

    @Autowired
    private DataFetcher referenceFetcher;

    public GraphQLObjectType create(GraphQLObjectType addressablePlaceObjectType) {
        return newObject()
                .name(OUTPUT_TYPE_ENTITY_REF)
                .description(ENTITY_REF_DESCRIPTION)
                .field(newFieldDefinition()
                        .name(ENTITY_REF_REF)
                        .type(GraphQLString))
                        .description(ENTITY_REF_REF_DESCRIPTION)
                .field(newFieldDefinition()
                        .name(ENTITY_REF_VERSION)
                        .type(GraphQLString))
                        .description(ENTITY_REF_VERSION_DESCRIPTION)
                .field(newFieldDefinition()
                        .name("addressablePlace")
                        .type(addressablePlaceObjectType)
                        .description("")
                        .dataFetcher(referenceFetcher))
                .build();
    }
}
