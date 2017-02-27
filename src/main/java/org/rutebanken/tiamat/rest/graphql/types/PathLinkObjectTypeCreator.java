package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.From;

import static graphql.Scalars.GraphQLInt;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geoJsonObjectType;

@Component
public class PathLinkObjectTypeCreator {

    public GraphQLObjectType create(GraphQLObjectType pathLinkEndObjecttype, GraphQLFieldDefinition netexIdFieldDefinition, GraphQLFieldDefinition geometryFieldDefinition) {
        return newObject()
                .name(OUTPUT_TYPE_PATH_LINK)
                .field(netexIdFieldDefinition)
                .field(newFieldDefinition()
                        .name(PATH_LINK_FROM)
                        .type(pathLinkEndObjecttype))
                .field(newFieldDefinition()
                        .name(PATH_LINK_TO)
                        .type(pathLinkEndObjecttype))
                .field(geometryFieldDefinition)
                .field(newFieldDefinition()
                        .name(TRANSFER_DURATION)
                        .type(newObject()
                                .name(OUTPUT_TYPE_TRANSFER_DURATION)
                                .description(TRANSFER_DURATION_DESCRIPTION)
                                .field(newFieldDefinition()
                                        .name(DEFAULT_DURATION)
                                        .type(GraphQLInt)
                                        .description(DEFAULT_DURATION_DESCRIPTION)
                                        .build())
                                .field(newFieldDefinition()
                                        .name(FREQUENT_TRAVELLER_DURATION)
                                        .type(GraphQLInt)
                                        .description(FREQUENT_TRAVELLER_DURATION_DESCRIPTION)
                                        .build())
                                .field(newFieldDefinition()
                                        .name(OCCASIONAL_TRAVELLER_DURATION)
                                        .type(GraphQLInt)
                                        .description(OCCASIONAL_TRAVELLER_DURATION_DESCRIPTION)
                                        .build())
                                .build())
                        .build())
                .build();
    }
}
