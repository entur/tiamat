package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_PATH_LINK_END;

@Component
public class PathLinkEndObjectTypeCreator {

    public GraphQLObjectType create(GraphQLObjectType quayObjectType, GraphQLObjectType stopPlaceObjectType, GraphQLFieldDefinition netexIdFieldDefinition) {
        return newObject()
                .name(OUTPUT_TYPE_PATH_LINK_END)
                .field(netexIdFieldDefinition)
                .field(newFieldDefinition()
                        .name("quay")
                        .type(quayObjectType))
                .field(newFieldDefinition()
                        .name("stopPlace")
                        .type(stopPlaceObjectType))
                .build();

    }
}
