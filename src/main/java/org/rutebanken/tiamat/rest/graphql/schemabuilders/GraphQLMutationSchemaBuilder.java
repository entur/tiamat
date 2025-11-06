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

package org.rutebanken.tiamat.rest.graphql.schemabuilders;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import org.rutebanken.tiamat.rest.graphql.registry.GraphQLOperationsRegistry;
import org.rutebanken.tiamat.rest.graphql.registry.GraphQLServicesRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.pathLinkObjectInputType;

/**
 * Builds the GraphQL mutation schema type.
 * Consolidates mutation field definitions and input type wiring.
 */
@Component
public class GraphQLMutationSchemaBuilder {

    @Autowired
    private GraphQLOperationsRegistry operationsRegistry;

    @Autowired
    private GraphQLServicesRegistry servicesRegistry;

    /**
     * Builds the root mutation type with all mutation operations.
     *
     * @param stopPlaceInterface StopPlace interface type
     * @param stopPlaceObjectType StopPlace object type
     * @param parentStopPlaceObjectType ParentStopPlace object type
     * @param groupOfStopPlacesObjectType GroupOfStopPlaces object type
     * @param purposeOfGroupingType PurposeOfGrouping object type
     * @param pathLinkObjectType PathLink object type
     * @param parkingObjectType Parking object type
     * @param tariffZoneObjectType TariffZone object type
     * @param stopPlaceInputObjectType StopPlace input type
     * @param parentStopPlaceInputObjectType ParentStopPlace input type
     * @param groupOfStopPlacesInputObjectType GroupOfStopPlaces input type
     * @param purposeOfGroupingInputObjectType PurposeOfGrouping input type
     * @param parkingInputObjectType Parking input type
     * @param validBetweenInputObjectType ValidBetween input type
     * @return Configured GraphQL mutation type
     */
    public GraphQLObjectType buildMutationType(
            GraphQLInterfaceType stopPlaceInterface,
            GraphQLObjectType stopPlaceObjectType,
            GraphQLObjectType parentStopPlaceObjectType,
            GraphQLObjectType groupOfStopPlacesObjectType,
            GraphQLObjectType purposeOfGroupingType,
            GraphQLObjectType pathLinkObjectType,
            GraphQLObjectType parkingObjectType,
            GraphQLObjectType tariffZoneObjectType,
            GraphQLInputObjectType stopPlaceInputObjectType,
            GraphQLInputObjectType parentStopPlaceInputObjectType,
            GraphQLInputObjectType groupOfStopPlacesInputObjectType,
            GraphQLInputObjectType purposeOfGroupingInputObjectType,
            GraphQLInputObjectType parkingInputObjectType,
            GraphQLInputObjectType validBetweenInputObjectType) {

        return newObject()
                .name(STOPPLACES_MUTATION)
                .description("Create and edit stopplaces")

                // StopPlace mutations
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(MUTATE_STOPPLACE)
                        .description("Create new or update existing StopPlace")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_STOPPLACE)
                                .type(stopPlaceInputObjectType)))

                .field(newFieldDefinition()
                        .type(new GraphQLList(parentStopPlaceObjectType))
                        .name(MUTATE_PARENT_STOPPLACE)
                        .description("Update existing Parent StopPlace")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_PARENT_STOPPLACE)
                                .type(parentStopPlaceInputObjectType)))

                // GroupOfStopPlaces mutations
                .field(newFieldDefinition()
                        .name(MUTATE_GROUP_OF_STOP_PLACES)
                        .type(groupOfStopPlacesObjectType)
                        .description("Mutate group of stop places")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_GROUP_OF_STOPPLACES)
                                .type(groupOfStopPlacesInputObjectType)))

                // PurposeOfGrouping mutations
                .field(newFieldDefinition()
                        .name(MUTATE_PURPOSE_OF_GROUPING)
                        .type(purposeOfGroupingType)
                        .description("Mutate purpose of grouping")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_PURPOSE_OF_GROUPING)
                                .type(purposeOfGroupingInputObjectType)))

                // PathLink mutations
                .field(newFieldDefinition()
                        .type(new GraphQLList(pathLinkObjectType))
                        .name(MUTATE_PATH_LINK)
                        .description("Create new or update existing PathLink")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_PATH_LINK)
                                .type(new GraphQLList(pathLinkObjectInputType)))
                        .description("Create new or update existing " + OUTPUT_TYPE_PATH_LINK))

                // Parking mutations
                .field(newFieldDefinition()
                        .type(new GraphQLList(parkingObjectType))
                        .name(MUTATE_PARKING)
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_PARKING)
                                .type(new GraphQLList(parkingInputObjectType)))
                        .description("Create new or update existing " + OUTPUT_TYPE_PARKING))

                // TariffZone termination
                .field(newFieldDefinition()
                        .type(tariffZoneObjectType)
                        .name(TERMINATE_TARIFF_ZONE)
                        .description("TariffZone will be terminated and no longer be active after the given date.")
                        .argument(newArgument().name(TARIFF_ZONE_ID).type(new GraphQLNonNull(GraphQLString)))
                        .argument(newArgument().name(VALID_BETWEEN_TO_DATE).type(new GraphQLNonNull(servicesRegistry.getDateScalar().getGraphQLDateScalar())))
                        .argument(newArgument().name(VERSION_COMMENT).type(GraphQLString)))

                // Tag operations
                .fields(operationsRegistry.getTagOperationsBuilder().getTagOperations())

                // StopPlace operations (delete, terminate, reopen, merge, move)
                .fields(operationsRegistry.getStopPlaceOperationsBuilder().getStopPlaceOperations(stopPlaceInterface))

                // Parking operations
                .fields(operationsRegistry.getParkingOperationsBuilder().getParkingOperations())

                // Multi-modality operations
                .fields(operationsRegistry.getMultiModalityOperationsBuilder().getMultiModalityOperations(parentStopPlaceObjectType, validBetweenInputObjectType))

                // Delete GroupOfStopPlaces
                .field(newFieldDefinition()
                        .type(GraphQLBoolean)
                        .name(DELETE_GROUP_OF_STOPPLACES)
                        .argument(GraphQLArgument.newArgument()
                                .name(ID)
                                .type(new GraphQLNonNull(GraphQLString)))
                        .description("Hard delete group of stop places by ID"))

                .build();
    }
}
