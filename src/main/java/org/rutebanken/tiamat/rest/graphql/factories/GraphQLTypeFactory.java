/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.rest.graphql.factories;

import graphql.schema.GraphQLType;

import java.util.List;

/**
 * Factory interface for creating GraphQL types.
 * Each factory is responsible for creating types related to a specific domain entity.
 * This pattern helps break down the monolithic schema builder into focused, testable components.
 */
public interface GraphQLTypeFactory {

    /**
     * Creates GraphQL types (object types, input types, enums, etc.) for this factory's domain.
     *
     * @return list of GraphQL types to be registered in the schema
     */
    List<GraphQLType> createTypes();

    /**
     * Returns a descriptive name for this factory, used for logging and debugging.
     *
     * @return factory name (e.g., "StopPlaceTypeFactory", "QuayTypeFactory")
     */
    String getFactoryName();
}