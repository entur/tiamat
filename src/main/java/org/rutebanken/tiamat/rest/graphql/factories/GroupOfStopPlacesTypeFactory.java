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

import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultilingualStringObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.netexIdFieldDefinition;

/**
 * Factory for creating GroupOfStopPlaces GraphQL output type.
 * GroupOfStopPlaces represents a logical grouping of stop places with a purpose.
 */
@Component
public class GroupOfStopPlacesTypeFactory implements GraphQLTypeFactory {

    @Override
    public List<GraphQLType> createTypes() {
        // Cannot create type without dependencies.
        // Use createGroupOfStopPlacesType(stopPlaceInterface, purposeOfGroupingType, entityPermissionObjectType) instead.
        throw new UnsupportedOperationException(
                "GroupOfStopPlaces requires dependencies. " +
                "Use createGroupOfStopPlacesType(GraphQLInterfaceType, GraphQLObjectType, GraphQLObjectType) instead.");
    }

    @Override
    public String getFactoryName() {
        return "GroupOfStopPlacesTypeFactory";
    }

    /**
     * Creates the output object type for GroupOfStopPlaces with required dependencies.
     *
     * @param stopPlaceInterface the StopPlace interface type
     * @param purposeOfGroupingType the PurposeOfGrouping object type
     * @param entityPermissionObjectType the EntityPermission object type
     * @return the GroupOfStopPlaces GraphQL output type
     */
    public GraphQLObjectType createGroupOfStopPlacesType(
            GraphQLInterfaceType stopPlaceInterface,
            GraphQLObjectType purposeOfGroupingType,
            GraphQLObjectType entityPermissionObjectType) {

        return newObject()
                .name(OUTPUT_TYPE_GROUP_OF_STOPPLACES)
                .field(netexIdFieldDefinition)
                .field(newFieldDefinition()
                        .name(NAME)
                        .type(embeddableMultilingualStringObjectType))
                .field(newFieldDefinition()
                        .name(SHORT_NAME)
                        .type(embeddableMultilingualStringObjectType))
                .field(newFieldDefinition()
                        .name(DESCRIPTION)
                        .type(embeddableMultilingualStringObjectType))
                .field(newFieldDefinition()
                        .name(VERSION)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(VERSION_COMMENT)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(PURPOSE_OF_GROUPING)
                        .type(purposeOfGroupingType))
                .field(newFieldDefinition()
                        .name(GROUP_OF_STOP_PLACES_MEMBERS)
                        .type(new GraphQLList(stopPlaceInterface)))
                .field(newFieldDefinition()
                        .name(PERMISSIONS)
                        .type(entityPermissionObjectType))
                .build();
    }
}