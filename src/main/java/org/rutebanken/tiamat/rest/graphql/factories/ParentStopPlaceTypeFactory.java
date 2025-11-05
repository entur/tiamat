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

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CHILDREN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_PARENT_STOPPLACE;

/**
 * Factory for creating ParentStopPlace GraphQL output type.
 * ParentStopPlace represents a stop place that contains child stop places.
 */
@Component
public class ParentStopPlaceTypeFactory implements GraphQLTypeFactory {

    @Override
    public List<GraphQLType> createTypes() {
        // Cannot create type without dependencies.
        // Use createParentStopPlaceType(...) instead.
        throw new UnsupportedOperationException(
                "ParentStopPlace requires StopPlaceInterface, field lists, and StopPlaceObjectType. " +
                "Use createParentStopPlaceType(GraphQLInterfaceType, List, List, GraphQLObjectType) instead.");
    }

    @Override
    public String getFactoryName() {
        return "ParentStopPlaceTypeFactory";
    }

    /**
     * Creates the output object type for ParentStopPlace with required dependencies.
     *
     * @param stopPlaceInterface the StopPlace interface type
     * @param stopPlaceInterfaceFields the fields from the StopPlace interface
     * @param commonFieldsList the common fields shared across types
     * @param stopPlaceObjectType the StopPlace object type
     * @return the ParentStopPlace GraphQL output type
     */
    public GraphQLObjectType createParentStopPlaceType(
            GraphQLInterfaceType stopPlaceInterface,
            List<GraphQLFieldDefinition> stopPlaceInterfaceFields,
            List<GraphQLFieldDefinition> commonFieldsList,
            GraphQLObjectType stopPlaceObjectType) {

        return newObject()
                .name(OUTPUT_TYPE_PARENT_STOPPLACE)
                .withInterface(stopPlaceInterface)
                .fields(stopPlaceInterfaceFields)
                .fields(commonFieldsList)
                .field(newFieldDefinition()
                        .name(CHILDREN)
                        .type(new GraphQLList(stopPlaceObjectType)))
                .build();
    }
}
