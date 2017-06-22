package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;

import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ENTITY_CLASSIFIER_ALL_TYPES;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

public class AuthorizationCheckCreator {

    public static List<GraphQLArgument> createAuthorizationCheckArguments() {
        List<GraphQLArgument> arguments = new ArrayList<>();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .description("The entity ID to check authorization for. For instance stop place ID")
                .build());
        return arguments;
    }

    public static GraphQLObjectType createAuthorizationCheckOutputType() {
        return newObject()
                .name(OUTPUT_TYPE_AUTHORIZATION_CHECK)
                .description(AUTHORIZATION_CHECK_DESCRIPTION)
                .field(newFieldDefinition()
                        .name(ID)
                        .type(GraphQLString)
                        .description("The identificatior for entity"))
                .field(newFieldDefinition()
                        .name(AUTHORIZATION_CHECK_ROLES)
                        .type(new GraphQLList(GraphQLString))
                        .description("The relevant roles for the given ID"))
                .build();
    }
}
