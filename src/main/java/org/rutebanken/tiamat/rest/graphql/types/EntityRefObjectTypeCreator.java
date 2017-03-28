package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ENTITY_REF_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ENTITY_REF_VERSION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_ENTITY_REF;

@Component
public class EntityRefObjectTypeCreator {

    @Autowired
    private DataFetcher referenceFetcher;

    public GraphQLObjectType create(GraphQLObjectType addressablePlaceObjectType) {
        return newObject()
                .name(OUTPUT_TYPE_ENTITY_REF)
                .description("A reference to an object")
                .field(newFieldDefinition()
                        .name(ENTITY_REF_REF)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(ENTITY_REF_VERSION)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name("addressablePlace")
                        .type(addressablePlaceObjectType)
                        .dataFetcher(referenceFetcher))
                .build();
    }
}
