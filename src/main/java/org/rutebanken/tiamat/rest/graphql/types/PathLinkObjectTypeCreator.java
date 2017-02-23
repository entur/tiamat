package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.From;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geoJsonObjectType;

@Component
public class PathLinkObjectTypeCreator {

    public GraphQLObjectType create(GraphQLObjectType pathLinkEndObjecttype, GraphQLFieldDefinition netexIdFieldDefinition) {
        return newObject()
                .name(OUTPUT_TYPE_PATH_LINK)
                .field(netexIdFieldDefinition)
                .field(newFieldDefinition()
                        .name(PATH_LINK_FROM)
                        .type(pathLinkEndObjecttype))
                .field(newFieldDefinition()
                        .name(PATH_LINK_TO)
                        .type(pathLinkEndObjecttype))
                .field(newFieldDefinition()
                        .name(GEOMETRY)
                        .type(geoJsonObjectType))
                .build();
    }
}
