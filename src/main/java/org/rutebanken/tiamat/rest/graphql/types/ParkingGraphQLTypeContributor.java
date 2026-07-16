/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in compliance with the Licence,
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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
