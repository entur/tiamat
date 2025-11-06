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

package org.rutebanken.tiamat.rest.graphql;

import graphql.language.BooleanValue;
import graphql.language.IntValue;
import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.TypeResolver;
import jakarta.annotation.PostConstruct;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.CycleStorageEquipment;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.GeneralSign;
import org.rutebanken.tiamat.model.Link;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SanitaryEquipment;
import org.rutebanken.tiamat.model.ShelterEquipment;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TicketingEquipment;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.model.WaitingRoomEquipment;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.rest.graphql.registry.GraphQLDataFetcherRegistry;
import org.rutebanken.tiamat.rest.graphql.registry.GraphQLOperationsRegistry;
import org.rutebanken.tiamat.rest.graphql.registry.GraphQLServicesRegistry;
import org.rutebanken.tiamat.rest.graphql.registry.GraphQLTypeRegistry;
import org.rutebanken.tiamat.rest.graphql.resolvers.MutableTypeResolver;
import org.rutebanken.tiamat.rest.graphql.types.StopPlaceInterfaceCreator;
import org.rutebanken.tiamat.rest.graphql.types.ZoneCommonFieldListCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.scalars.ExtendedScalars.GraphQLBigDecimal;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;
import static org.rutebanken.tiamat.rest.graphql.operations.MultiModalityOperationsBuilder.ADD_TO_MULTI_MODAL_STOP_PLACE_INPUT;
import static org.rutebanken.tiamat.rest.graphql.scalars.TransportModeScalar.getValidSubmodes;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.accessibilityAssessmentObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.createParkingInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.createParkingObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultiLingualStringInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.equipmentType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geometryFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.getEquipmentOfType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.interchangeWeightingEnum;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.modificationEnumerationType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.netexIdFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.pathLinkObjectInputType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.privateCodeFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.scopingMethodEnumType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.stopPlaceTypeEnum;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.submodeEnum;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.topographicPlaceInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.topographicPlaceTypeEnum;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.transportModeSubmodeObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.versionLessRefInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.versionValidityEnumType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.zoneTopologyEnumType;

@Component
public class StopPlaceRegisterGraphQLSchema {

    public final static int DEFAULT_PAGE_VALUE = 0;
    public final static int DEFAULT_SIZE_VALUE = 20;

    public GraphQLSchema stopPlaceRegisterSchema;

    // Registries that consolidate related dependencies
    @Autowired
    private GraphQLTypeRegistry typeRegistry;

    @Autowired
    private GraphQLOperationsRegistry operationsRegistry;

    @Autowired
    private GraphQLDataFetcherRegistry dataFetcherRegistry;

    @Autowired
    private GraphQLServicesRegistry servicesRegistry;

    // Supporting components that don't fit into registries
    @Autowired
    private ZoneCommonFieldListCreator zoneCommonFieldListCreator;

    @Autowired
    private StopPlaceInterfaceCreator stopPlaceInterfaceCreator;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    @PostConstruct
    public void init() {

        /**
         * Common field list for quays, stop places and addressable place.
         */
        List<GraphQLFieldDefinition> commonFieldsList = new ArrayList<>();
        commonFieldsList.add(newFieldDefinition()
                .name(PLACE_EQUIPMENTS)
                .type(equipmentType)
                .build());
        commonFieldsList.add(newFieldDefinition()
                .name(ACCESSIBILITY_ASSESSMENT)
                .description("This field is set either on StopPlace (i.e. all Quays are equal), or on every Quay.")
                .type(accessibilityAssessmentObjectType)
                .build()
        );

        commonFieldsList.add(newFieldDefinition()
                        .name(PUBLIC_CODE)
                        .type(GraphQLString).build());
        commonFieldsList.add(privateCodeFieldDefinition);
        commonFieldsList.add(
                newFieldDefinition()
                        .name(MODIFICATION_ENUMERATION)
                        .type(modificationEnumerationType)
                        .build()
        );

        // Get ValidBetween types from factory - returns [objectType, inputType]
        List<GraphQLType> validBetweenTypes = typeRegistry.getValidBetweenTypeFactory().createTypes();
        GraphQLObjectType validBetweenObjectType = (GraphQLObjectType) validBetweenTypes.getFirst();
        GraphQLObjectType userPermissionsObjectType = newObject()
                .name(OUTPUT_TYPE_USER_PERMISSIONS)
                .field(newFieldDefinition()
                        .name("isGuest")
                        .type(GraphQLBoolean)
                        .build())
                .field(newFieldDefinition()
                        .name("allowNewStopEverywhere")
                        .type(GraphQLBoolean)
                        .build())
                .field(newFieldDefinition()
                        .name("preferredName")
                        .type(GraphQLString)
                        .build())
                .build();

        GraphQLObjectType entityPermissionObjectType = newObject()
                .name(OUTPUT_TYPE_ENTITY_PERMISSIONS)
                .field(newFieldDefinition()
                        .name("canEdit")
                        .type(GraphQLBoolean)
                        .build())
                .field(newFieldDefinition()
                        .name("canDelete")
                        .type(GraphQLBoolean)
                        .build())

                .field(newFieldDefinition()
                        .name("allowedStopPlaceTypes")
                        .type(new GraphQLList(stopPlaceTypeEnum))
                        .build())
                .field(newFieldDefinition()
                        .name("bannedStopPlaceTypes")
                        .type(new GraphQLList(stopPlaceTypeEnum))
                        .build())
                .field(newFieldDefinition()
                        .name("allowedSubmodes")
                        .type(new GraphQLList(submodeEnum))
                        .build())
                .field(newFieldDefinition()
                        .name("bannedSubmodes")
                        .type(new GraphQLList(submodeEnum))
                        .build())
                .build();

        List<GraphQLFieldDefinition> zoneCommandFieldList = zoneCommonFieldListCreator.create(validBetweenObjectType);

        commonFieldsList.addAll(zoneCommandFieldList);

        // Get Quay object type from factory with merged commonFieldsList (includes zone fields)
        List<GraphQLType> quayTypes = typeRegistry.getQuayTypeFactory().createQuayTypes(commonFieldsList);
        GraphQLObjectType quayObjectType = (GraphQLObjectType) quayTypes.getFirst();


        // Get TopographicPlace type from factory
        GraphQLObjectType topographicPlaceObjectType = (GraphQLObjectType) typeRegistry.getTopographicPlaceTypeFactory().createTypes().getFirst();

        // Get TariffZone and FareZone types from factories with zone common fields
        GraphQLObjectType tariffZoneObjectType = typeRegistry.getTariffZoneTypeFactory().createTariffZoneType(zoneCommandFieldList);
        GraphQLObjectType fareZoneObjectType = typeRegistry.getFareZoneTypeFactory().createFareZoneType(zoneCommandFieldList);

        MutableTypeResolver stopPlaceTypeResolver = new MutableTypeResolver();

        List<GraphQLFieldDefinition> stopPlaceInterfaceFields = stopPlaceInterfaceCreator.createCommonInterfaceFields(tariffZoneObjectType,fareZoneObjectType, topographicPlaceObjectType, validBetweenObjectType, entityPermissionObjectType);
        GraphQLInterfaceType stopPlaceInterface = stopPlaceInterfaceCreator.createInterface(stopPlaceInterfaceFields, commonFieldsList);

        GraphQLObjectType stopPlaceObjectType = typeRegistry.getStopPlaceTypeFactory().createStopPlaceType(stopPlaceInterface, stopPlaceInterfaceFields, commonFieldsList, quayObjectType);
        GraphQLObjectType parentStopPlaceObjectType = typeRegistry.getParentStopPlaceTypeFactory().createParentStopPlaceType(stopPlaceInterface, stopPlaceInterfaceFields, commonFieldsList, stopPlaceObjectType);

        stopPlaceTypeResolver.setResolveFunction(object -> {
            if(object instanceof StopPlace stopPlace) {
                if(stopPlace.isParentStopPlace()) {
                    return parentStopPlaceObjectType;
                } else {
                    return stopPlaceObjectType;
                }
            }
            throw new IllegalArgumentException("StopPlaceTypeResolver cannot resolve type of Object " + object + ". Was expecting StopPlace");
        });

        // Get PurposeOfGrouping, GroupOfStopPlaces, and GroupOfTariffZones types from factories
        GraphQLObjectType purposeOfGroupingType = (GraphQLObjectType) typeRegistry.getPurposeOfGroupingTypeFactory().createTypes().getFirst();
        GraphQLObjectType groupOfStopPlacesObjectType = typeRegistry.getGroupOfStopPlacesTypeFactory().createGroupOfStopPlacesType(stopPlaceInterface, purposeOfGroupingType, entityPermissionObjectType);
        GraphQLObjectType groupOfTariffZonesObjectType = (GraphQLObjectType) typeRegistry.getGroupOfTariffZonesTypeFactory().createTypes().getFirst();

        // Get AddressablePlace object type from factory with merged commonFieldsList
        GraphQLObjectType addressablePlaceObjectType = typeRegistry.getAddressablePlaceTypeFactory().createAddressablePlaceType(commonFieldsList);

        // Get EntityRef, PathLinkEnd, and PathLink types from factories
        GraphQLObjectType entityRefObjectType = typeRegistry.getEntityRefTypeFactory().createEntityRefType(addressablePlaceObjectType);
        GraphQLObjectType pathLinkEndObjectType = typeRegistry.getPathLinkEndTypeFactory().createPathLinkEndType(entityRefObjectType, netexIdFieldDefinition);
        GraphQLObjectType pathLinkObjectType = typeRegistry.getPathLinkTypeFactory().createPathLinkType(pathLinkEndObjectType, netexIdFieldDefinition, geometryFieldDefinition);

        GraphQLObjectType parkingObjectType = createParkingObjectType(validBetweenObjectType);

        GraphQLArgument allVersionsArgument = GraphQLArgument.newArgument()
                .name(ALL_VERSIONS)
                .type(GraphQLBoolean)
                .description(ALL_VERSIONS_ARG_DESCRIPTION)
                .build();


        GraphQLObjectType stopPlaceRegisterQuery = newObject()
                .name(STOPPLACES_REGISTER)
                .description("Query and search for data")
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceInterface))
                        .name(FIND_STOPPLACE)
                        .description("Search for StopPlaces")
                        .arguments(createFindStopPlaceArguments(allVersionsArgument))
                       )
                        //Search by BoundingBox
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceInterface))
                        .name(FIND_STOPPLACE_BY_BBOX)
                        .description("Find StopPlaces within given BoundingBox.")
                        .arguments(createBboxArguments())
                        )
                .field(newFieldDefinition()
                        .name(FIND_TOPOGRAPHIC_PLACE)
                        .type(new GraphQLList(topographicPlaceObjectType))
                        .description("Find topographic places")
                        .arguments(createFindTopographicPlaceArguments(allVersionsArgument))
                        )
                .field(newFieldDefinition()
                        .name(FIND_PATH_LINK)
                        .type(new GraphQLList(pathLinkObjectType))
                        .description("Find path links")
                        .arguments(createFindPathLinkArguments(allVersionsArgument))
                        )
                .field(newFieldDefinition()
                        .name(FIND_PARKING)
                        .type(new GraphQLList(parkingObjectType))
                        .description("Find parking")
                        .arguments(createFindParkingArguments(allVersionsArgument))
                        )
                .field(newFieldDefinition()
                        .name(VALID_TRANSPORT_MODES)
                        .type(new GraphQLList(transportModeSubmodeObjectType))
                        .description("List all valid Transportmode/Submode-combinations."))
                .field(newFieldDefinition()
                        .name(TAGS)
                        .type(new GraphQLList(typeRegistry.getTagTypeFactory().createTypes().getFirst()))
                        .description(TAGS_DESCRIPTION)
                        .argument(GraphQLArgument.newArgument()
                            .name(TAG_NAME)
                            .description(TAG_NAME_DESCRIPTION)
                            .type(new GraphQLNonNull(GraphQLString)))
                        .build())
                .field(newFieldDefinition()
                        .name(GROUP_OF_STOP_PLACES)
                        .type(new GraphQLList(groupOfStopPlacesObjectType))
                        .description("Group of stop places")
                        .arguments(createFindGroupOfStopPlacesArguments())
                        .build())
                .field(newFieldDefinition()
                        .name(PURPOSE_OF_GROUPING)
                        .type(new GraphQLList(purposeOfGroupingType))
                        .description("List all purpose of grouping")
                        .arguments(createFindPurposeOfGroupingArguments()))

                .field(newFieldDefinition()
                        .name(GROUP_OF_TARIFF_ZONES)
                        .type(new GraphQLList(groupOfTariffZonesObjectType))
                        .description("Group of tariff zones")
                        .arguments(createFindGroupOfTariffZonesArguments())
                        .build())
                .field(newFieldDefinition()
                        .name(TARIFF_ZONES)
                        .type(new GraphQLList(tariffZoneObjectType))
                        .description("Tariff zones")
                        .arguments(createFindTariffZonesArguments())
                        .build())
                .field(newFieldDefinition()
                        .name(FARE_ZONES)
                        .type(new GraphQLList(fareZoneObjectType))
                        .description("Fare zones")
                        .arguments(createFindFareZonesArguments())
                        .build())
                .field(newFieldDefinition()
                        .name(FARE_ZONES_AUTHORITIES)
                        .type(new GraphQLList(GraphQLString))
                        .description("List all fare zone authorities.")
                        .build())
                .field(newFieldDefinition()
                                        .name(USER_PERMISSIONS)
                                        .description("User permissions")
                                        .type(userPermissionsObjectType)

                        .build())
                .field(newFieldDefinition()
                        .name(LOCATION_PERMISSIONS)
                        .description("Location permissions")
                        .type(entityPermissionObjectType)
                        .arguments(createLocationArguments()))
                .build();


        // Get common input fields from factory
        List<GraphQLInputObjectField> commonInputFieldList = typeRegistry.getCommonFieldsFactory().createCommonInputFieldList(embeddableMultiLingualStringInputObjectType);

        // Get Quay input type from factory (second element in list)
        GraphQLInputObjectType quayInputObjectType = (GraphQLInputObjectType) quayTypes.get(1);

        // Get ValidBetween input type from factory (second element in list)
        GraphQLInputObjectType validBetweenInputObjectType = (GraphQLInputObjectType) validBetweenTypes.get(1);

        GraphQLInputObjectType stopPlaceInputObjectType = createStopPlaceInputObjectType(commonInputFieldList,
                topographicPlaceInputObjectType, quayInputObjectType, validBetweenInputObjectType);

        GraphQLInputObjectType parentStopPlaceInputObjectType = typeRegistry.getParentStopPlaceInputTypeFactory().createParentStopPlaceInputType(commonInputFieldList, validBetweenInputObjectType, stopPlaceInputObjectType);

        GraphQLInputObjectType parkingInputObjectType = createParkingInputObjectType(validBetweenInputObjectType);

        // Get GroupOfStopPlaces input type from factory
        GraphQLInputObjectType groupOfStopPlacesInputObjectType = (GraphQLInputObjectType) typeRegistry.getGroupOfStopPlacesInputTypeFactory().createTypes().getFirst();

        // Get PurposeOfGrouping input type from factory
        GraphQLInputObjectType purposeOfGroupingInputObjectType = (GraphQLInputObjectType) typeRegistry.getPurposeOfGroupingInputTypeFactory().createTypes().getFirst();

        GraphQLObjectType stopPlaceRegisterMutation = newObject()
                .name(STOPPLACES_MUTATION)
                .description("Create and edit stopplaces")
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(MUTATE_STOPPLACE)
                        .description("Create new or update existing StopPlace")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_STOPPLACE)
                                .type(stopPlaceInputObjectType))
                        )
                .field(newFieldDefinition()
                        .type(new GraphQLList(parentStopPlaceObjectType))
                        .name(MUTATE_PARENT_STOPPLACE)
                        .description("Update existing Parent StopPlace")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_PARENT_STOPPLACE)
                                .type(parentStopPlaceInputObjectType))
                        )
                .field(newFieldDefinition()
                        .name(MUTATE_GROUP_OF_STOP_PLACES)
                        .type(groupOfStopPlacesObjectType)
                        .description("Mutate group of stop places")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_GROUP_OF_STOPPLACES)
                                .type(groupOfStopPlacesInputObjectType))
                        )
                .field(newFieldDefinition()
                        .name(MUTATE_PURPOSE_OF_GROUPING)
                        .type(purposeOfGroupingType)
                        .description("Mutate purpose of grouping")
                        .argument(GraphQLArgument.newArgument().
                                name(OUTPUT_TYPE_PURPOSE_OF_GROUPING)
                                .type(purposeOfGroupingInputObjectType))
                        )
                .field(newFieldDefinition()
                        .type(new GraphQLList(pathLinkObjectType))
                        .name(MUTATE_PATH_LINK)
                        .description("Create new or update existing PathLink")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_PATH_LINK)
                                .type(new GraphQLList(pathLinkObjectInputType)))
                        .description("Create new or update existing " + OUTPUT_TYPE_PATH_LINK)
                        )
                .field(newFieldDefinition()
                        .type(new GraphQLList(parkingObjectType))
                        .name(MUTATE_PARKING)
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_PARKING)
                                .type(new GraphQLList(parkingInputObjectType)))
                        .description("Create new or update existing " + OUTPUT_TYPE_PARKING))
                .field(newFieldDefinition()
                        .type(tariffZoneObjectType)
                        .name(TERMINATE_TARIFF_ZONE)
                        .description("TariffZone will be terminated and no longer be active after the given date.")
                        .argument(newArgument().name(TARIFF_ZONE_ID).type(new GraphQLNonNull(GraphQLString)))
                        .argument(newArgument().name(VALID_BETWEEN_TO_DATE).type(new GraphQLNonNull(servicesRegistry.getDateScalar().getGraphQLDateScalar())))
                        .argument(newArgument().name(VERSION_COMMENT).type(GraphQLString))
                        )
                .fields(operationsRegistry.getTagOperationsBuilder().getTagOperations())
                .fields(operationsRegistry.getStopPlaceOperationsBuilder().getStopPlaceOperations(stopPlaceInterface))
                .fields(operationsRegistry.getParkingOperationsBuilder().getParkingOperations())
                .fields(operationsRegistry.getMultiModalityOperationsBuilder().getMultiModalityOperations(parentStopPlaceObjectType, validBetweenInputObjectType))
                .field(newFieldDefinition()
                        .type(GraphQLBoolean)
                        .name(DELETE_GROUP_OF_STOPPLACES)
                        .argument(GraphQLArgument.newArgument()
                                .name(ID)
                                .type(new GraphQLNonNull(GraphQLString)))
                        .description("Hard delete group of stop places by ID")
                        )
                .build();

        stopPlaceRegisterSchema = GraphQLSchema.newSchema()
                .query(stopPlaceRegisterQuery)
                .mutation(stopPlaceRegisterMutation)
                .codeRegistry(buildCodeRegistry(stopPlaceTypeResolver))
                .build();
    }

    public GraphQLCodeRegistry buildCodeRegistry(TypeResolver stopPlaceTypeResolver) {
        GraphQLCodeRegistry.Builder codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry();
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, IMPORTED_ID, getOriginalIdsFetcher());

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, CHANGED_BY, getChangedByFetcher(authorizationService));
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, CHANGED_BY, getChangedByFetcher(authorizationService));

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, IMPORTED_ID, getOriginalIdsFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_QUAY, IMPORTED_ID, getOriginalIdsFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, ID, getNetexIdFetcher());

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, PERMISSIONS, dataFetcherRegistry.getEntityPermissionsFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, PERMISSIONS, dataFetcherRegistry.getEntityPermissionsFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_GROUP_OF_STOPPLACES, PERMISSIONS, dataFetcherRegistry.getEntityPermissionsFetcher());

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, TARIFF_ZONES, dataFetcherRegistry.getStopPlaceTariffZoneFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, TARIFF_ZONES, dataFetcherRegistry.getStopPlaceTariffZoneFetcher());

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_ACCESSIBILITY_ASSESSMENT, ID, getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_ACCESSIBILITY_ASSESSMENT , LIMITATIONS,
                env -> {
                    List<AccessibilityLimitation> limitations = ((AccessibilityAssessment) env.getSource()).getLimitations();
                    if (limitations != null && !limitations.isEmpty()) {
                        return limitations.getFirst();
                    }
                    return null;
                }
                );

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_ACCESSIBILITY_LIMITATIONS , ID, getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_ACCESSIBILITY_LIMITATIONS , ID, getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_PURPOSE_OF_GROUPING , ID, getNetexIdFetcher());

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, TAGS, dataFetcherRegistry.getTagFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, TAGS, dataFetcherRegistry.getTagFetcher());

        dataFetcherGeometry(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE);
        dataFetcherGeometry(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE);
        dataFetcherGeometry(codeRegistryBuilder, OUTPUT_TYPE_QUAY);
        dataFetcherGeometry(codeRegistryBuilder, OUTPUT_TYPE_PARKING);
        dataFetcherGeometry(codeRegistryBuilder, OUTPUT_TYPE_TARIFF_ZONE);
        dataFetcherGeometry(codeRegistryBuilder, OUTPUT_TYPE_FARE_ZONE);
        dataFetcherGeometry(codeRegistryBuilder, OUTPUT_TYPE_BOARDING_POSITION);


        dataFetcherPlaceEquipments(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE);
        dataFetcherPlaceEquipments(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE);
        dataFetcherPlaceEquipments(codeRegistryBuilder, OUTPUT_TYPE_QUAY);


        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, STOP_PLACE_GROUPS, dataFetcherRegistry.getStopPlaceGroupsFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, STOP_PLACE_GROUPS, dataFetcherRegistry.getStopPlaceGroupsFetcher());


        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, FARE_ZONES, dataFetcherRegistry.getStopPlaceFareZoneFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, FARE_ZONES, dataFetcherRegistry.getStopPlaceFareZoneFetcher());

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, ID, getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_QUAY, ID, getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_GROUP_OF_STOPPLACES,ID,getNetexIdFetcher());

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TARIFF_ZONE,ID,getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_FARE_ZONE,ID,getNetexIdFetcher());

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_PARKING,ID,getNetexIdFetcher());

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, KEY_VALUES, dataFetcherRegistry.getKeyValuesDataFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, KEY_VALUES, dataFetcherRegistry.getKeyValuesDataFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_TARIFF_ZONE, KEY_VALUES, dataFetcherRegistry.getKeyValuesDataFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_FARE_ZONE, KEY_VALUES, dataFetcherRegistry.getKeyValuesDataFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_QUAY, KEY_VALUES, dataFetcherRegistry.getKeyValuesDataFetcher());

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, POLYGON, dataFetcherRegistry.getPolygonFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, POLYGON, dataFetcherRegistry.getPolygonFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_TARIFF_ZONE, POLYGON, dataFetcherRegistry.getPolygonFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_FARE_ZONE, POLYGON, dataFetcherRegistry.getPolygonFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_QUAY, POLYGON, dataFetcherRegistry.getPolygonFetcher());

        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, VALID_TRANSPORT_MODES, env -> servicesRegistry.getTransportModeScalar().getConfiguredTransportModes().keySet());

        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FIND_STOPPLACE, dataFetcherRegistry.getStopPlaceFetcher());
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FIND_STOPPLACE_BY_BBOX, dataFetcherRegistry.getStopPlaceFetcher());
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FIND_TOPOGRAPHIC_PLACE, dataFetcherRegistry.getTopographicPlaceFetcher());
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FIND_PATH_LINK, dataFetcherRegistry.getPathLinkFetcher());
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FIND_PARKING, dataFetcherRegistry.getParkingFetcher());
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, TAGS, dataFetcherRegistry.getTagFetcher());
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, GROUP_OF_STOP_PLACES, dataFetcherRegistry.getGroupOfStopPlacesFetcher());
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, PURPOSE_OF_GROUPING, dataFetcherRegistry.getPurposeOfGroupingFetcher());
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, GROUP_OF_TARIFF_ZONES, dataFetcherRegistry.getGroupOfTariffZonesFetcher());
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, TARIFF_ZONES, dataFetcherRegistry.getTariffZonesFetcher());
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FARE_ZONES, dataFetcherRegistry.getFareZonesFetcher());
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FARE_ZONES_AUTHORITIES, dataFetcherRegistry.getFareZoneAuthoritiesFetcher());


        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_GEO_JSON, TYPE, env -> {
            if (env.getSource() instanceof Geometry geometry) {
                return geometry.getClass().getSimpleName();
            }
            return null;
        });

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_PLACE_EQUIPMENTS,ID,getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_PLACE_EQUIPMENTS,WAITING_ROOM_EQUIPMENT,env -> getEquipmentOfType(WaitingRoomEquipment.class, env));
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_PLACE_EQUIPMENTS,SANITARY_EQUIPMENT,env -> getEquipmentOfType(SanitaryEquipment.class, env));
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_PLACE_EQUIPMENTS,TICKETING_EQUIPMENT,env -> getEquipmentOfType(TicketingEquipment.class, env));
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_PLACE_EQUIPMENTS,SHELTER_EQUIPMENT,env -> getEquipmentOfType(ShelterEquipment.class, env));
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_PLACE_EQUIPMENTS,CYCLE_STORAGE_EQUIPMENT,env -> getEquipmentOfType(CycleStorageEquipment.class, env));
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_PLACE_EQUIPMENTS,GENERAL_SIGN,env -> getEquipmentOfType(GeneralSign.class, env));
        registerDataFetcher(codeRegistryBuilder,"TransportModes","transportMode",env -> env.getSource());
        registerDataFetcher(codeRegistryBuilder,"TransportModes","submode",env ->getValidSubmodes(env.getSource()));
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_PARKING,PARENT_SITE_REF,env -> {
            SiteRefStructure parentSiteRef = ((Parking) env.getSource()).getParentSiteRef();
            if (parentSiteRef != null) {
                return parentSiteRef.getRef();
            }
            return null;
        });

        mapNetexId(codeRegistryBuilder, OUTPUT_TYPE_SHELTER_EQUIPMENT, OUTPUT_TYPE_SANITARY_EQUIPMENT, OUTPUT_TYPE_CYCLE_STORAGE_EQUIPMENT, OUTPUT_TYPE_GENERAL_SIGN_EQUIPMENT, OUTPUT_TYPE_TICKETING_EQUIPMENT, OUTPUT_TYPE_WAITING_ROOM_EQUIPMENT);
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_BOARDING_POSITION,ID,getNetexIdFetcher());

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_ENTITY_REF,ADDRESSABLE_PLACE,dataFetcherRegistry.getReferenceFetcher());

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_FARE_ZONE,FARE_ZONES_AUTHORITY_REF,env -> env.getSource() instanceof FareZone fareZone ? fareZone.getTransportOrganisationRef() : null);
        // Use factory for FareZone data fetchers
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_FARE_ZONE,FARE_ZONES_NEIGHBOURS,env -> typeRegistry.getFareZoneTypeFactory().fareZoneNeighboursType(env));
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_FARE_ZONE,FARE_ZONES_MEMBERS,env -> typeRegistry.getFareZoneTypeFactory().fareZoneMemberType(env));

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_GROUP_OF_STOPPLACES,PURPOSE_OF_GROUPING,dataFetcherRegistry.getGroupOfStopPlacesPurposeOfGroupingFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_GROUP_OF_STOPPLACES,GROUP_OF_STOP_PLACES_MEMBERS,dataFetcherRegistry.getGroupOfStopPlacesMembersFetcher());

        // Use factory for GroupOfTariffZones data fetchers
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_GROUP_OF_TARIFF_ZONES,GROUP_OF_TARIFF_ZONES_MEMBERS,env -> typeRegistry.getGroupOfTariffZonesTypeFactory().groupOfTariffZoneMembersType(env));
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_GROUP_OF_TARIFF_ZONES,ID,getNetexIdFetcher());

        // Use factory for PathLink data fetchers
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TRANSFER_DURATION,DEFAULT_DURATION,typeRegistry.getPathLinkTypeFactory().durationSecondsFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TRANSFER_DURATION,FREQUENT_TRAVELLER_DURATION,typeRegistry.getPathLinkTypeFactory().durationSecondsFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TRANSFER_DURATION,OCCASIONAL_TRAVELLER_DURATION,typeRegistry.getPathLinkTypeFactory().durationSecondsFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TRANSFER_DURATION,MOBILITY_RESTRICTED_TRAVELLER_DURATION,typeRegistry.getPathLinkTypeFactory().durationSecondsFetcher());

        // topographic place data fetchers
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TOPOGRAPHIC_PLACE,ID,env -> {
            TopographicPlace topographicPlace = (TopographicPlace) env.getSource();
            if (topographicPlace != null) {
                return topographicPlace.getNetexId();
            } else {
                return null;
            }
        });
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TOPOGRAPHIC_PLACE,PARENT_TOPOGRAPHIC_PLACE,env -> {
            if(env.getSource() instanceof  TopographicPlace topographicPlace) {
                if(topographicPlace.getParentTopographicPlaceRef() != null) {
                    return servicesRegistry.getTopographicPlaceRepository().findFirstByNetexIdAndVersion(topographicPlace.getParentTopographicPlaceRef().getRef(), Long.parseLong(topographicPlace.getParentTopographicPlaceRef().getVersion()));
                }
            }
            return null;
        });
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TOPOGRAPHIC_PLACE,POLYGON,dataFetcherRegistry.getPolygonFetcher());

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_STOPPLACE,SUBMODE,env -> servicesRegistry.getTransportModeScalar().resolveSubmode(env));
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_STOPPLACE,PARENT_SITE_REF,env -> {
            SiteRefStructure parentSiteRef = ((StopPlace) env.getSource()).getParentSiteRef();
            if (parentSiteRef != null) {
                return parentSiteRef.getRef();
            }
            return null;
        });

        registerDataFetcher(codeRegistryBuilder,STOPPLACES_REGISTER,USER_PERMISSIONS,dataFetcherRegistry.getUserPermissionsFetcher());
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_REGISTER,LOCATION_PERMISSIONS,dataFetcherRegistry.getLocationPermissionsFetcher());


        //mutation

        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MUTATE_PARKING,dataFetcherRegistry.getParkingUpdater());
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MUTATE_STOPPLACE,dataFetcherRegistry.getStopPlaceUpdater());
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MUTATE_PARENT_STOPPLACE,dataFetcherRegistry.getStopPlaceUpdater());
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MUTATE_GROUP_OF_STOP_PLACES,dataFetcherRegistry.getGroupOfStopPlacesUpdater());
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MUTATE_PURPOSE_OF_GROUPING,dataFetcherRegistry.getPurposeOfGroupingUpdater());
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MUTATE_PATH_LINK,dataFetcherRegistry.getPathLinkUpdater());
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,TERMINATE_TARIFF_ZONE, env -> servicesRegistry.getTariffZoneTerminator().terminateTariffZone(env.getArgument(TARIFF_ZONE_ID), env.getArgument(VALID_BETWEEN_TO_DATE), env.getArgument(VERSION_COMMENT)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,DELETE_GROUP_OF_STOPPLACES,dataFetcherRegistry.getGroupOfStopPlacesDeleterFetcher());
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,CREATE_MULTI_MODAL_STOPPLACE,
                environment -> {
                    Map input = environment.getArgument("input");

                    if(input == null) {
                        throw new IllegalArgumentException("input is not specified");
                    }

                    ValidBetween validBetween = servicesRegistry.getValidBetweenMapper().map((Map) input.get(VALID_BETWEEN));
                    String versionComment = (String) input.get(VERSION_COMMENT);
                    Point geoJsonPoint = servicesRegistry.getGeometryMapper().createGeoJsonPoint((Map) input.get(GEOMETRY));
                    EmbeddableMultilingualString name = getEmbeddableString((Map) input.get(NAME));

                    @SuppressWarnings("unchecked")
                    List<String> stopPlaceIds = (List<String>) input.get(STOP_PLACE_IDS);

                    return servicesRegistry.getParentStopPlaceEditor().createMultiModalParentStopPlace(stopPlaceIds, name, validBetween, versionComment, geoJsonPoint);
                }
                    );

        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,ADD_TO_MULTIMODAL_STOPPLACE,
                environment -> {
                    Map input = environment.getArgument("input");

                    if(input == null) {
                        throw new IllegalArgumentException("input is not specified");
                    }

                    if(input.get(PARENT_SITE_REF) == null) {
                        throw new IllegalArgumentException("Parent site ref cannot be null for this operation" + ADD_TO_MULTI_MODAL_STOP_PLACE_INPUT);
                    }

                    String parentSiteRef = (String) input.get(PARENT_SITE_REF);

                    ValidBetween validBetween = servicesRegistry.getValidBetweenMapper().map((Map) input.get(VALID_BETWEEN));
                    String versionComment = (String) input.get(VERSION_COMMENT);

                    if(input.get(STOP_PLACE_IDS) == null) {
                        throw new IllegalArgumentException("List of " + STOP_PLACE_IDS + "cannot be null");
                    }
                    @SuppressWarnings("unchecked")
                    List<String> stopPlaceIds = (List<String>) input.get(STOP_PLACE_IDS);

                    return servicesRegistry.getParentStopPlaceEditor().addToMultiModalParentStopPlace(parentSiteRef, stopPlaceIds, validBetween, versionComment);
                }
        );
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,REMOVE_FROM_MULTIMODAL_STOPPLACE,
                environment -> servicesRegistry.getParentStopPlaceEditor().removeFromMultiModalStopPlace(environment.getArgument(PARENT_SITE_REF), environment.getArgument(STOP_PLACE_ID))
        );


        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,DELETE_PARKING, environment -> servicesRegistry.getParkingDeleter().deleteParking(environment.getArgument(PARKING_ID)));


        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MERGE_STOP_PLACES,environment -> servicesRegistry.getStopPlaceMerger().mergeStopPlaces(environment.getArgument(FROM_STOP_PLACE_ID), environment.getArgument(TO_STOP_PLACE_ID), environment.getArgument(FROM_VERSION_COMMENT), environment.getArgument(TO_VERSION_COMMENT), environment.getArgument(DRY_RUN)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MERGE_QUAYS,environment -> servicesRegistry.getStopPlaceQuayMerger().mergeQuays(environment.getArgument(STOP_PLACE_ID), environment.getArgument(FROM_QUAY_ID), environment.getArgument(TO_QUAY_ID), environment.getArgument(VERSION_COMMENT), environment.getArgument(DRY_RUN)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MOVE_QUAYS_TO_STOP,environment -> servicesRegistry.getStopPlaceQuayMover().moveQuays(environment.getArgument(QUAY_IDS), environment.getArgument(TO_STOP_PLACE_ID), environment.getArgument(FROM_VERSION_COMMENT), environment.getArgument(TO_VERSION_COMMENT)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,DELETE_STOP_PLACE,environment -> servicesRegistry.getStopPlaceDeleter().deleteStopPlace(environment.getArgument(STOP_PLACE_ID)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,TERMINATE_STOP_PLACE,environment -> servicesRegistry.getStopPlaceTerminator().terminateStopPlace(environment.getArgument(STOP_PLACE_ID), environment.getArgument(VALID_BETWEEN_TO_DATE), environment.getArgument(VERSION_COMMENT) , environment.getArgument(MODIFICATION_ENUMERATION)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,REOPEN_STOP_PLACE,environment -> servicesRegistry.getStopPlaceReopener().reopenStopPlace(environment.getArgument(STOP_PLACE_ID), environment.getArgument(VERSION_COMMENT)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,DELETE_QUAY_FROM_STOP_PLACE,environment -> servicesRegistry.getStopPlaceQuayDeleter().deleteQuay(environment.getArgument(STOP_PLACE_ID), environment.getArgument(QUAY_ID), environment.getArgument(VERSION_COMMENT)));

        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,REMOVE_TAG,environment -> servicesRegistry.getTagRemover().removeTag(environment.getArgument(TAG_NAME), environment.getArgument(TAG_ID_REFERENCE), environment.getArgument(TAG_COMMENT)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,CREATE_TAG,environment -> servicesRegistry.getTagCreator().createTag(environment.getArgument(TAG_NAME), environment.getArgument(TAG_ID_REFERENCE), environment.getArgument(TAG_COMMENT)));

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_GEO_JSON, LEGACY_COORDINATES,getLegacyCoordinates());

        codeRegistryBuilder.typeResolver(OUTPUT_TYPE_STOPPLACE_INTERFACE, stopPlaceTypeResolver);

        return codeRegistryBuilder.build();
    }

    private void mapNetexId(GraphQLCodeRegistry.Builder codeRegistryBuilder, String outputTypeShelterEquipment, String outputTypeSanitaryEquipment, String outputTypeCycleStorageEquipment, String outputTypeGeneralSignEquipment, String outputTypeTicketingEquipment, String outputTypeWaitingRoomEquipment) {
        registerDataFetcher(codeRegistryBuilder, outputTypeShelterEquipment,ID,getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder, outputTypeSanitaryEquipment,ID,getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder, outputTypeCycleStorageEquipment,ID,getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder, outputTypeGeneralSignEquipment,ID,getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder, outputTypeTicketingEquipment,ID,getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder, outputTypeWaitingRoomEquipment,ID,getNetexIdFetcher());
    }

    private void dataFetcherPlaceEquipments(GraphQLCodeRegistry.Builder codeRegistryBuilder, String source) {
        registerDataFetcher(codeRegistryBuilder, source, PLACE_EQUIPMENTS, env -> {
            if (env.getSource() instanceof StopPlace stopPlace) {
                return stopPlace.getPlaceEquipments();
            } else if (env.getSource() instanceof Quay quay) {
                return quay.getPlaceEquipments();
            }
            return null;
        });
    }

    private void dataFetcherGeometry(GraphQLCodeRegistry.Builder codeRegistryBuilder, String parentType) {
        registerDataFetcher(codeRegistryBuilder, parentType, GEOMETRY, env -> {
            if (env.getSource() instanceof Zone_VersionStructure source) {
                if (source.getCentroid()!=null) {
                    return source.getCentroid();
                }
                return source.getPolygon();
            } else if (env.getSource() instanceof Link link) {
                return link.getLineString();
            }
            return null;
        });
    }


    private static DataFetcher<Object> getNetexIdFetcher() {
        return env -> {
            if (env.getSource() instanceof IdentifiedEntity identifiedEntity) {
                return identifiedEntity.getNetexId();
            }
            return null;
        };
    }

    private static DataFetcher<Object> getLegacyCoordinates() {
        return env -> {
            if(env.getSource() instanceof Polygon polygon) {
                return polygon.getCoordinates();
            }
            if (env.getSource() instanceof Point point) {
                return point.getCoordinates();
            }
            if (env.getSource() instanceof LineString lineString) {
                return lineString.getCoordinates();
            }
            return null;
        };
    }

    private static DataFetcher<Object> getOriginalIdsFetcher(){
        return env -> {
            if(env.getSource() instanceof DataManagedObjectStructure dataManagedObjectStructure){
                return dataManagedObjectStructure.getOriginalIds();
            }
            return null;
        };
    }

    private static DataFetcher<Object> getChangedByFetcher(AuthorizationService authorizationService) {
        return env -> {
            if(env.getSource() instanceof DataManagedObjectStructure dataManagedObjectStructure && !authorizationService.isGuest()){
                return dataManagedObjectStructure.getChangedBy();
            }
            return null;
        };
    }

    private void registerDataFetcher(GraphQLCodeRegistry.Builder codeRegistryBuilder, String parentType, String fieldName, DataFetcher<?> dataFetcher) {
        FieldCoordinates coordinates = FieldCoordinates.coordinates(parentType, fieldName);
        codeRegistryBuilder.dataFetcher(coordinates, dataFetcher);
    }

    private List<GraphQLArgument> createFindPathLinkArguments(GraphQLArgument allVersionsArgument) {
        List<GraphQLArgument> arguments = new ArrayList<>();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .build());
        arguments.add(allVersionsArgument);
        arguments.add(GraphQLArgument.newArgument()
                .name(FIND_BY_STOP_PLACE_ID)
                .type(GraphQLString)
                .build());
        return arguments;
    }

    private List<GraphQLArgument> createFindParkingArguments(GraphQLArgument allVersionsArgument) {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(VERSION)
                .type(GraphQLInt)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(FIND_BY_STOP_PLACE_ID)
                .type(GraphQLString)
                .build());
        arguments.add(allVersionsArgument);
        return arguments;
    }

    private List<GraphQLArgument> createFindGroupOfStopPlacesArguments() {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(FIND_BY_STOP_PLACE_ID)
                .type(GraphQLString)
                .build());
        return arguments;
    }

    private List<GraphQLArgument> createFindPurposeOfGroupingArguments(){
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .build());
        return arguments;
    }

    private List<GraphQLArgument> createFindGroupOfTariffZonesArguments() {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(FIND_BY_TARIFF_ZONE_ID)
                .type(GraphQLString)
                .build());
        return arguments;
    }

    private List<GraphQLArgument> createFindTariffZonesArguments() {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(IDS)
                .type(new GraphQLList(GraphQLString))
                .build());
        return arguments;
    }

    private List<GraphQLArgument> createFindFareZonesArguments() {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(IDS)
                .type(new GraphQLList(GraphQLString))
                .build());

        arguments.add(GraphQLArgument.newArgument()
                .name(FARE_ZONES_AUTHORITY_REF)
                .type(GraphQLString)
                .build());

        arguments.add(GraphQLArgument.newArgument()
                .name(FARE_ZONES_SCOPING_METHOD)
                .type(scopingMethodEnumType)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(FARE_ZONES_ZONE_TOPOLOGY)
                .type(zoneTopologyEnumType)
                .build());

        return arguments;
    }

    private List<GraphQLArgument> createFindTopographicPlaceArguments(GraphQLArgument allVersionsArgument) {
        List<GraphQLArgument> arguments = new ArrayList<>();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .build());
        arguments.add(allVersionsArgument);
        arguments.add(GraphQLArgument.newArgument()
                .name(TOPOGRAPHIC_PLACE_TYPE)
                .type(topographicPlaceTypeEnum)
                .description("Limits results to specified placeType.")
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .description("Searches for TopographicPlaces by name.")
                .build());
        return arguments;
    }

    private List<GraphQLArgument> createFindStopPlaceArguments(GraphQLArgument allVersionsArgument) {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(allVersionsArgument);
                //Search
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .description(ID_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(VERSION)
                .type(GraphQLInt)
                .description(VERSION_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .type(versionValidityEnumType)
                .name(VERSION_VALIDITY_ARG)
                .description(VERSION_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(STOP_PLACE_TYPE)
                .type(new GraphQLList(stopPlaceTypeEnum))
                .description(STOP_PLACE_TYPE_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(COUNTY_REF)
                .type(new GraphQLList(GraphQLString))
                .description(COUNTY_REF_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(COUNTRY_REF)
                .type(new GraphQLList(GraphQLString))
                .description(COUNTRY_REF_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(TAGS)
                .type(new GraphQLList(GraphQLString))
                .description(TAGS_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(MUNICIPALITY_REF)
                .type(new GraphQLList(GraphQLString))
                .description(MUNICIPALITY_REF_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .description(QUERY_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(IMPORTED_ID_QUERY)
                .type(GraphQLString)
                .description(IMPORTED_ID_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(POINT_IN_TIME)
                .type(servicesRegistry.getDateScalar().getGraphQLDateScalar())
                .description(POINT_IN_TIME_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(KEY)
                .type(GraphQLString)
                .description(KEY_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(WITHOUT_LOCATION_ONLY)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(WITHOUT_LOCATION_ONLY_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(WITHOUT_QUAYS_ONLY)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(WITHOUT_QUAYS_ONLY_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(WITH_DUPLICATED_QUAY_IMPORTED_IDS)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(WITH_DUPLICATED_QUAY_IMPORTED_IDS_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(WITH_NEARBY_SIMILAR_DUPLICATES)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(WITH_NEARBY_SIMILAR_DUPLICATES_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(HAS_PARKING)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(HAS_PARKING)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(ONLY_MONOMODAL_STOPPLACES)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(ONLY_MONOMODAL_STOPPLACES_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(VALUES)
                .type(new GraphQLList(GraphQLString))
                .description(VALUES_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(WITH_TAGS)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(WITH_TAGS_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(SEARCH_WITH_CODE_SPACE)
                .type(GraphQLString)
                .description(SEARCH_WITH_CODE_SPACE_ARG_DESCRIPTION)
                .build());
        return arguments;
    }

    private List<GraphQLArgument> createLocationArguments() {
        List<GraphQLArgument> arguments = new ArrayList<>();
        arguments.add(GraphQLArgument.newArgument()
                .name(LONGITUDE)
                .description("longitude")
                .type(new GraphQLNonNull(GraphQLBigDecimal))
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(LATITUDE)
                .description("latitude")
                .type(new GraphQLNonNull(GraphQLBigDecimal))
                .build());

        return arguments;

    }

    private List<GraphQLArgument> createBboxArguments() {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
                //BoundingBox
        arguments.add(GraphQLArgument.newArgument()
                .name(LONGITUDE_MIN)
                .description("Bottom left longitude (xMin).")
                .type(new GraphQLNonNull(GraphQLBigDecimal))
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(LATITUDE_MIN)
                .description("Bottom left latitude (yMin).")
                .type(new GraphQLNonNull(GraphQLBigDecimal))
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(LONGITUDE_MAX)
                .description("Top right longitude (xMax).")
                .type(new GraphQLNonNull(GraphQLBigDecimal))
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(LATITUDE_MAX)
                .description("Top right longitude (yMax).")
                .type(new GraphQLNonNull(GraphQLBigDecimal))
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(IGNORE_STOPPLACE_ID)
                .type(GraphQLString)
                .description("ID of StopPlace to excluded from result.")
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(INCLUDE_EXPIRED)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description("Set to true if expired StopPlaces should be returned, default is 'false'.")
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(POINT_IN_TIME)
                .type(servicesRegistry.getDateScalar().getGraphQLDateScalar())
                .description(POINT_IN_TIME_ARG_DESCRIPTION)
                .build());
        return arguments;
    }

    private List<GraphQLArgument> createPageAndSizeArguments() {
        List<GraphQLArgument> arguments = new ArrayList<>();
        arguments.add(GraphQLArgument.newArgument()
                .name(PAGE)
                .type(GraphQLInt)
                .defaultValueLiteral(IntValue.of(DEFAULT_PAGE_VALUE))
                .description(PAGE_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(SIZE)
                .type(GraphQLInt)
                .defaultValueLiteral(IntValue.of(DEFAULT_SIZE_VALUE))
                .description(SIZE_ARG_DESCRIPTION)
                .build());
        return arguments;
    }


    private GraphQLInputObjectType createStopPlaceInputObjectType(List<GraphQLInputObjectField> commonInputFieldsList,
                                                                  GraphQLInputObjectType topographicPlaceInputObjectType,
                                                                  GraphQLInputObjectType quayObjectInputType,
                                                                  GraphQLInputObjectType validBetweenInputObjectType) {
        return newInputObject()
                .name(INPUT_TYPE_STOPPLACE)
                .fields(commonInputFieldsList)
                .fields(servicesRegistry.getTransportModeScalar().createTransportModeInputFieldsList())
                .field(newInputObjectField()
                        .name(STOP_PLACE_TYPE)
                        .type(stopPlaceTypeEnum))
                .field(newInputObjectField()
                        .name(TOPOGRAPHIC_PLACE)
                        .type(topographicPlaceInputObjectType))
                .field(newInputObjectField()
                        .name(WEIGHTING)
                        .type(interchangeWeightingEnum))
                .field(newInputObjectField()
                        .name(PARENT_SITE_REF)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(VERSION_COMMENT)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(QUAYS)
                        .type(new GraphQLList(quayObjectInputType)))
                .field(newInputObjectField()
                        .name(VALID_BETWEEN)
                        .type(validBetweenInputObjectType))
                .field(newInputObjectField()
                        .name(TARIFF_ZONES)
                        .description("List of tariff zone references without version")
                        .type(new GraphQLList(versionLessRefInputObjectType)))
                .field(newInputObjectField()
                        .name(ADJACENT_SITES)
                        .description(ADJACENT_SITES_DESCRIPTION)
                        .type(new GraphQLList(versionLessRefInputObjectType)))
                .field(newInputObjectField()
                        .name(URL)
                        .type(GraphQLString).build())
                .build();
    }
}