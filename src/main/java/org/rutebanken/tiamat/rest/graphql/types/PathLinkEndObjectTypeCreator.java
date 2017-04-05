package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_PATH_LINK_END;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PLACE_REF;

@Component
public class PathLinkEndObjectTypeCreator {

    public GraphQLObjectType create(GraphQLObjectType entityReferenceObjectType, GraphQLFieldDefinition netexIdFieldDefinition) {
        return newObject()
                .name(OUTPUT_TYPE_PATH_LINK_END)
                .field(netexIdFieldDefinition)
                .field(newFieldDefinition()
                        .name(PLACE_REF)
                        .type(entityReferenceObjectType))
                .build();
    }
}
