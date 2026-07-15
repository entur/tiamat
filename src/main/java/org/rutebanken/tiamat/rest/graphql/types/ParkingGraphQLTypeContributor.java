package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLObjectType;

/**
 * Extension point allowing deployments to contribute additional fields to the
 * GraphQL parking output and input types without modifying core.
 * <p>
 * Implementations registered as Spring beans are collected by
 * {@link org.rutebanken.tiamat.rest.graphql.StopPlaceRegisterGraphQLSchema}
 * and applied before the parking types are built.
 * <p>
 * Any enum types required by contributed fields must be registered separately
 * (e.g. via {@link graphql.schema.GraphQLEnumType}) in the same contributor.
 */
public interface ParkingGraphQLTypeContributor {

    /**
     * Add fields to the parking query/response output type.
     *
     * @param builder the builder for {@code Parking} output type, before {@code .build()} is called
     */
    void contributeToOutputType(GraphQLObjectType.Builder builder);

    /**
     * Add fields to the parking mutation input type.
     *
     * @param builder the builder for {@code Parking} input type, before {@code .build()} is called
     */
    void contributeToInputType(GraphQLInputObjectType.Builder builder);
}
