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
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import org.rutebanken.tiamat.rest.graphql.argumentbuilders.GraphQLArgumentsFactory;
import org.rutebanken.tiamat.rest.graphql.registry.GraphQLTypeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.transportModeSubmodeObjectType;

/**
 * Builds the GraphQL query schema type.
 * Consolidates query field definitions and argument wiring.
 */
@Component
public class GraphQLQuerySchemaBuilder {

    @Autowired
    private GraphQLArgumentsFactory argumentsFactory;

    @Autowired
    private GraphQLTypeRegistry typeRegistry;

    /**
     * Builds the root query type with all query operations.
     *
     * @param stopPlaceInterface StopPlace interface type
     * @param topographicPlaceObjectType TopographicPlace object type
     * @param pathLinkObjectType PathLink object type
     * @param parkingObjectType Parking object type
     * @param groupOfStopPlacesObjectType GroupOfStopPlaces object type
     * @param purposeOfGroupingType PurposeOfGrouping object type
     * @param groupOfTariffZonesObjectType GroupOfTariffZones object type
     * @param tariffZoneObjectType TariffZone object type
     * @param fareZoneObjectType FareZone object type
     * @param userPermissionsObjectType UserPermissions object type
     * @param entityPermissionObjectType EntityPermission object type
     * @param allVersionsArgument Argument for including all versions
     * @return Configured GraphQL query type
     */
    public GraphQLObjectType buildQueryType(
            GraphQLInterfaceType stopPlaceInterface,
            GraphQLObjectType topographicPlaceObjectType,
            GraphQLObjectType pathLinkObjectType,
            GraphQLObjectType parkingObjectType,
            GraphQLObjectType groupOfStopPlacesObjectType,
            GraphQLObjectType purposeOfGroupingType,
            GraphQLObjectType groupOfTariffZonesObjectType,
            GraphQLObjectType tariffZoneObjectType,
            GraphQLObjectType fareZoneObjectType,
            GraphQLObjectType userPermissionsObjectType,
            GraphQLObjectType entityPermissionObjectType,
            GraphQLArgument allVersionsArgument) {

        return newObject()
                .name(STOPPLACES_REGISTER)
                .description("Query and search for data")

                // StopPlace queries
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceInterface))
                        .name(FIND_STOPPLACE)
                        .description("Search for StopPlaces")
                        .arguments(argumentsFactory.createFindStopPlaceArguments(allVersionsArgument)))

                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceInterface))
                        .name(FIND_STOPPLACE_BY_BBOX)
                        .description("Find StopPlaces within given BoundingBox.")
                        .arguments(argumentsFactory.createBboxArguments()))

                // TopographicPlace queries
                .field(newFieldDefinition()
                        .name(FIND_TOPOGRAPHIC_PLACE)
                        .type(new GraphQLList(topographicPlaceObjectType))
                        .description("Find topographic places")
                        .arguments(argumentsFactory.createFindTopographicPlaceArguments(allVersionsArgument)))

                // PathLink queries
                .field(newFieldDefinition()
                        .name(FIND_PATH_LINK)
                        .type(new GraphQLList(pathLinkObjectType))
                        .description("Find path links")
                        .arguments(argumentsFactory.createFindPathLinkArguments(allVersionsArgument)))

                // Parking queries
                .field(newFieldDefinition()
                        .name(FIND_PARKING)
                        .type(new GraphQLList(parkingObjectType))
                        .description("Find parking")
                        .arguments(argumentsFactory.createFindParkingArguments(allVersionsArgument)))

                // Transport modes
                .field(newFieldDefinition()
                        .name(VALID_TRANSPORT_MODES)
                        .type(new GraphQLList(transportModeSubmodeObjectType))
                        .description("List all valid Transportmode/Submode-combinations."))

                // Tags
                .field(newFieldDefinition()
                        .name(TAGS)
                        .type(new GraphQLList(typeRegistry.getTagTypeFactory().createTypes().getFirst()))
                        .description(TAGS_DESCRIPTION)
                        .argument(GraphQLArgument.newArgument()
                                .name(TAG_NAME)
                                .description(TAG_NAME_DESCRIPTION)
                                .type(new GraphQLNonNull(GraphQLString)))
                        .build())

                // GroupOfStopPlaces queries
                .field(newFieldDefinition()
                        .name(GROUP_OF_STOP_PLACES)
                        .type(new GraphQLList(groupOfStopPlacesObjectType))
                        .description("Group of stop places")
                        .arguments(argumentsFactory.createFindGroupOfStopPlacesArguments())
                        .build())

                // PurposeOfGrouping queries
                .field(newFieldDefinition()
                        .name(PURPOSE_OF_GROUPING)
                        .type(new GraphQLList(purposeOfGroupingType))
                        .description("List all purpose of grouping")
                        .arguments(argumentsFactory.createFindPurposeOfGroupingArguments()))

                // GroupOfTariffZones queries
                .field(newFieldDefinition()
                        .name(GROUP_OF_TARIFF_ZONES)
                        .type(new GraphQLList(groupOfTariffZonesObjectType))
                        .description("Group of tariff zones")
                        .arguments(argumentsFactory.createFindGroupOfTariffZonesArguments())
                        .build())

                // TariffZones queries
                .field(newFieldDefinition()
                        .name(TARIFF_ZONES)
                        .type(new GraphQLList(tariffZoneObjectType))
                        .description("Tariff zones")
                        .arguments(argumentsFactory.createFindTariffZonesArguments())
                        .build())

                // FareZones queries
                .field(newFieldDefinition()
                        .name(FARE_ZONES)
                        .type(new GraphQLList(fareZoneObjectType))
                        .description("Fare zones")
                        .arguments(argumentsFactory.createFindFareZonesArguments())
                        .build())

                .field(newFieldDefinition()
                        .name(FARE_ZONES_AUTHORITIES)
                        .type(new GraphQLList(GraphQLString))
                        .description("List all fare zone authorities.")
                        .build())

                // Permissions queries
                .field(newFieldDefinition()
                        .name(USER_PERMISSIONS)
                        .description("User permissions")
                        .type(userPermissionsObjectType)
                        .build())

                .field(newFieldDefinition()
                        .name(LOCATION_PERMISSIONS)
                        .description("Location permissions")
                        .type(entityPermissionObjectType)
                        .arguments(argumentsFactory.createLocationArguments()))

                .build();
    }
}
