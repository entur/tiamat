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

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
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
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;
import static org.rutebanken.tiamat.rest.graphql.operations.MultiModalityOperationsBuilder.ADD_TO_MULTI_MODAL_STOP_PLACE_INPUT;
import static org.rutebanken.tiamat.rest.graphql.scalars.TransportModeScalar.getValidSubmodes;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.accessibilityAssessmentObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.createParkingObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultiLingualStringInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.equipmentType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geometryFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.getEquipmentOfType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.modificationEnumerationType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.netexIdFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.privateCodeFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.stopPlaceTypeEnum;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.submodeEnum;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.topographicPlaceInputObjectType;

@Component
public class StopPlaceRegisterGraphQLSchema {

    public GraphQLSchema stopPlaceRegisterSchema;

    // Registries that consolidate related dependencies
    @Autowired
    private GraphQLTypeRegistry typeRegistry;

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
    private org.rutebanken.tiamat.rest.graphql.schemabuilders.GraphQLInputTypesBuilder inputTypesBuilder;

    @Autowired
    private org.rutebanken.tiamat.rest.graphql.schemabuilders.GraphQLQuerySchemaBuilder querySchemaBuilder;

    @Autowired
    private org.rutebanken.tiamat.rest.graphql.schemabuilders.GraphQLMutationSchemaBuilder mutationSchemaBuilder;

    @Autowired
    @PostConstruct
    public void init() {
        // Build common field definitions
        List<GraphQLFieldDefinition> commonFieldsList = buildCommonFieldsList();

        // Create ValidBetween, permission, and zone types
        List<GraphQLType> validBetweenTypes = typeRegistry.getValidBetweenTypeFactory().createTypes();
        GraphQLObjectType validBetweenObjectType = (GraphQLObjectType) validBetweenTypes.getFirst();
        GraphQLInputObjectType validBetweenInputObjectType = (GraphQLInputObjectType) validBetweenTypes.get(1);

        GraphQLObjectType userPermissionsObjectType = buildUserPermissionsObjectType();
        GraphQLObjectType entityPermissionObjectType = buildEntityPermissionObjectType();

        // Create zone fields and add to common fields
        List<GraphQLFieldDefinition> zoneCommandFieldList = zoneCommonFieldListCreator.create(validBetweenObjectType);
        commonFieldsList.addAll(zoneCommandFieldList);

        // Build all object types via type factories
        List<GraphQLType> quayTypes = typeRegistry.getQuayTypeFactory().createQuayTypes(commonFieldsList);
        GraphQLObjectType quayObjectType = (GraphQLObjectType) quayTypes.getFirst();
        GraphQLInputObjectType quayInputObjectType = (GraphQLInputObjectType) quayTypes.get(1);

        List<GraphQLType> topographicPlaceTypes = typeRegistry.getTopographicPlaceTypeFactory().createTypes();
        GraphQLObjectType topographicPlaceObjectType = (GraphQLObjectType) topographicPlaceTypes.getFirst();

        GraphQLObjectType tariffZoneObjectType = typeRegistry.getTariffZoneTypeFactory().createTariffZoneType(zoneCommandFieldList);
        GraphQLObjectType fareZoneObjectType = typeRegistry.getFareZoneTypeFactory().createFareZoneType(zoneCommandFieldList);

        // Build StopPlace interface and types
        MutableTypeResolver stopPlaceTypeResolver = new MutableTypeResolver();
        List<GraphQLFieldDefinition> stopPlaceInterfaceFields = stopPlaceInterfaceCreator.createCommonInterfaceFields(
                tariffZoneObjectType, fareZoneObjectType, topographicPlaceObjectType, validBetweenObjectType, entityPermissionObjectType);
        GraphQLInterfaceType stopPlaceInterface = stopPlaceInterfaceCreator.createInterface(stopPlaceInterfaceFields, commonFieldsList);

        GraphQLObjectType stopPlaceObjectType = typeRegistry.getStopPlaceTypeFactory().createStopPlaceType(
                stopPlaceInterface, stopPlaceInterfaceFields, commonFieldsList, quayObjectType);
        GraphQLObjectType parentStopPlaceObjectType = typeRegistry.getParentStopPlaceTypeFactory().createParentStopPlaceType(
                stopPlaceInterface, stopPlaceInterfaceFields, commonFieldsList, stopPlaceObjectType);

        stopPlaceTypeResolver.setResolveFunction(object -> {
            if (object instanceof StopPlace stopPlace) {
                return stopPlace.isParentStopPlace() ? parentStopPlaceObjectType : stopPlaceObjectType;
            }
            throw new IllegalArgumentException("StopPlaceTypeResolver cannot resolve type of Object " + object + ". Was expecting StopPlace");
        });

        // Build grouping and zone types
        GraphQLObjectType purposeOfGroupingType = (GraphQLObjectType) typeRegistry.getPurposeOfGroupingTypeFactory().createTypes().getFirst();
        GraphQLObjectType groupOfStopPlacesObjectType = typeRegistry.getGroupOfStopPlacesTypeFactory().createGroupOfStopPlacesType(
                stopPlaceInterface, purposeOfGroupingType, entityPermissionObjectType);
        GraphQLObjectType groupOfTariffZonesObjectType = (GraphQLObjectType) typeRegistry.getGroupOfTariffZonesTypeFactory().createTypes().getFirst();

        // Build path link and related types
        GraphQLObjectType addressablePlaceObjectType = typeRegistry.getAddressablePlaceTypeFactory().createAddressablePlaceType(commonFieldsList);
        GraphQLObjectType entityRefObjectType = typeRegistry.getEntityRefTypeFactory().createEntityRefType(addressablePlaceObjectType);
        GraphQLObjectType pathLinkEndObjectType = typeRegistry.getPathLinkEndTypeFactory().createPathLinkEndType(
                entityRefObjectType, netexIdFieldDefinition);
        GraphQLObjectType pathLinkObjectType = typeRegistry.getPathLinkTypeFactory().createPathLinkType(
                pathLinkEndObjectType, netexIdFieldDefinition, geometryFieldDefinition);

        GraphQLObjectType parkingObjectType = createParkingObjectType(validBetweenObjectType);

        // Create allVersions argument
        GraphQLArgument allVersionsArgument = GraphQLArgument.newArgument()
                .name(ALL_VERSIONS)
                .type(GraphQLBoolean)
                .description(ALL_VERSIONS_ARG_DESCRIPTION)
                .build();

        // Build input types via input types builder
        List<GraphQLInputObjectField> commonInputFieldList = typeRegistry.getCommonFieldsFactory()
                .createCommonInputFieldList(embeddableMultiLingualStringInputObjectType);
        Map<String, GraphQLInputObjectType> inputTypes = inputTypesBuilder.buildInputTypes(
                commonInputFieldList, topographicPlaceInputObjectType, quayInputObjectType, validBetweenInputObjectType);

        // Build query schema
        GraphQLObjectType stopPlaceRegisterQuery = querySchemaBuilder.buildQueryType(
                stopPlaceInterface, topographicPlaceObjectType, pathLinkObjectType, parkingObjectType,
                groupOfStopPlacesObjectType, purposeOfGroupingType, groupOfTariffZonesObjectType,
                tariffZoneObjectType, fareZoneObjectType, userPermissionsObjectType,
                entityPermissionObjectType, allVersionsArgument);

        // Build mutation schema
        GraphQLObjectType stopPlaceRegisterMutation = mutationSchemaBuilder.buildMutationType(
                stopPlaceInterface, stopPlaceObjectType, parentStopPlaceObjectType, groupOfStopPlacesObjectType,
                purposeOfGroupingType, pathLinkObjectType, parkingObjectType, tariffZoneObjectType,
                inputTypes.get(INPUT_TYPE_STOPPLACE), inputTypes.get(INPUT_TYPE_PARENT_STOPPLACE),
                inputTypes.get(INPUT_TYPE_GROUP_OF_STOPPLACES), inputTypes.get(INPUT_TYPE_PURPOSE_OF_GROUPING),
                inputTypes.get(INPUT_TYPE_PARKING), validBetweenInputObjectType);

        // Build final GraphQL schema
        stopPlaceRegisterSchema = GraphQLSchema.newSchema()
                .query(stopPlaceRegisterQuery)
                .mutation(stopPlaceRegisterMutation)
                .codeRegistry(buildCodeRegistry(stopPlaceTypeResolver))
                .build();
    }

    /**
     * Builds common field definitions for stop places, quays, and addressable places.
     * Includes place equipments, accessibility assessment, public/private codes, and modification enumeration.
     *
     * @return List of common field definitions
     */
    private List<GraphQLFieldDefinition> buildCommonFieldsList() {
        List<GraphQLFieldDefinition> commonFieldsList = new ArrayList<>();
        commonFieldsList.add(newFieldDefinition()
                .name(PLACE_EQUIPMENTS)
                .type(equipmentType)
                .build());
        commonFieldsList.add(newFieldDefinition()
                .name(ACCESSIBILITY_ASSESSMENT)
                .description("This field is set either on StopPlace (i.e. all Quays are equal), or on every Quay.")
                .type(accessibilityAssessmentObjectType)
                .build());
        commonFieldsList.add(newFieldDefinition()
                .name(PUBLIC_CODE)
                .type(GraphQLString)
                .build());
        commonFieldsList.add(privateCodeFieldDefinition);
        commonFieldsList.add(newFieldDefinition()
                .name(MODIFICATION_ENUMERATION)
                .type(modificationEnumerationType)
                .build());
        return commonFieldsList;
    }

    /**
     * Builds the UserPermissions object type.
     *
     * @return UserPermissions GraphQL object type
     */
    private GraphQLObjectType buildUserPermissionsObjectType() {
        return newObject()
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
    }

    /**
     * Builds the EntityPermission object type.
     *
     * @return EntityPermission GraphQL object type
     */
    private GraphQLObjectType buildEntityPermissionObjectType() {
        return newObject()
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
    }

    /**
     * Data fetcher for creating a multi-modal parent stop place.
     * Validates input and delegates to MultiModalStopPlaceEditor.
     *
     * @param environment GraphQL environment containing input arguments
     * @return Created parent stop place
     * @throws IllegalArgumentException if input validation fails
     */
    private StopPlace createMultiModalStopPlaceFetcher(DataFetchingEnvironment environment) {
        Map input = environment.getArgument("input");

        if (input == null) {
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

    /**
     * Data fetcher for adding stop places to an existing multi-modal parent.
     * Validates input and delegates to MultiModalStopPlaceEditor.
     *
     * @param environment GraphQL environment containing input arguments
     * @return Updated parent stop place
     * @throws IllegalArgumentException if input validation fails
     */
    private StopPlace addToMultiModalStopPlaceFetcher(DataFetchingEnvironment environment) {
        Map input = environment.getArgument("input");

        if (input == null) {
            throw new IllegalArgumentException("input is not specified");
        }

        if (input.get(PARENT_SITE_REF) == null) {
            throw new IllegalArgumentException("Parent site ref cannot be null for this operation: " + ADD_TO_MULTI_MODAL_STOP_PLACE_INPUT);
        }

        String parentSiteRef = (String) input.get(PARENT_SITE_REF);
        ValidBetween validBetween = servicesRegistry.getValidBetweenMapper().map((Map) input.get(VALID_BETWEEN));
        String versionComment = (String) input.get(VERSION_COMMENT);

        if (input.get(STOP_PLACE_IDS) == null) {
            throw new IllegalArgumentException("List of " + STOP_PLACE_IDS + " cannot be null");
        }

        @SuppressWarnings("unchecked")
        List<String> stopPlaceIds = (List<String>) input.get(STOP_PLACE_IDS);

        return servicesRegistry.getParentStopPlaceEditor().addToMultiModalParentStopPlace(parentSiteRef, stopPlaceIds, validBetween, versionComment);
    }

    /**
     * Fetches the first accessibility limitation from an AccessibilityAssessment.
     * Returns null if no limitations exist.
     *
     * @return DataFetcher for accessibility limitations
     */
    private static DataFetcher<AccessibilityLimitation> getAccessibilityLimitationsFetcher() {
        return env -> {
            List<AccessibilityLimitation> limitations = ((AccessibilityAssessment) env.getSource()).getLimitations();
            if (limitations != null && !limitations.isEmpty()) {
                return limitations.getFirst();
            }
            return null;
        };
    }

    /**
     * Fetches the geometry type name for GeoJSON output.
     * Returns the simple class name of the Geometry subclass.
     *
     * @return DataFetcher for GeoJSON type
     */
    private static DataFetcher<String> getGeoJsonTypeFetcher() {
        return env -> {
            if (env.getSource() instanceof Geometry geometry) {
                return geometry.getClass().getSimpleName();
            }
            return null;
        };
    }

    /**
     * Fetches the parent site reference string from a Parking entity.
     * Returns null if parentSiteRef is not set.
     *
     * @return DataFetcher for parking parent site reference
     */
    private static DataFetcher<String> getParkingParentSiteRefFetcher() {
        return env -> {
            SiteRefStructure parentSiteRef = ((Parking) env.getSource()).getParentSiteRef();
            if (parentSiteRef != null) {
                return parentSiteRef.getRef();
            }
            return null;
        };
    }

    /**
     * Fetches the NetEx ID from a TopographicPlace entity.
     * Returns null if source is not a TopographicPlace.
     *
     * @return DataFetcher for topographic place ID
     */
    private static DataFetcher<String> getTopographicPlaceIdFetcher() {
        return env -> {
            TopographicPlace topographicPlace = (TopographicPlace) env.getSource();
            if (topographicPlace != null) {
                return topographicPlace.getNetexId();
            } else {
                return null;
            }
        };
    }

    /**
     * Fetches the parent topographic place from a TopographicPlace entity.
     * Looks up parent by reference if parentTopographicPlaceRef is set.
     *
     * @return DataFetcher for parent topographic place
     */
    private DataFetcher<TopographicPlace> getParentTopographicPlaceFetcher() {
        return env -> {
            if (env.getSource() instanceof TopographicPlace topographicPlace) {
                if (topographicPlace.getParentTopographicPlaceRef() != null) {
                    return servicesRegistry.getTopographicPlaceRepository().findFirstByNetexIdAndVersion(
                            topographicPlace.getParentTopographicPlaceRef().getRef(),
                            Long.parseLong(topographicPlace.getParentTopographicPlaceRef().getVersion())
                    );
                }
            }
            return null;
        };
    }

    /**
     * Fetches the parent site reference string from a StopPlace entity.
     * Returns null if parentSiteRef is not set.
     *
     * @return DataFetcher for stop place parent site reference
     */
    private static DataFetcher<String> getStopPlaceParentSiteRefFetcher() {
        return env -> {
            SiteRefStructure parentSiteRef = ((StopPlace) env.getSource()).getParentSiteRef();
            if (parentSiteRef != null) {
                return parentSiteRef.getRef();
            }
            return null;
        };
    }

    /**
     * Registers data fetchers for common entity fields shared across multiple types.
     * Includes: ID, importedId, changedBy, permissions fields.
     *
     * @param builder The code registry builder to register fetchers on
     */
    private void registerEntityCommonFields(GraphQLCodeRegistry.Builder builder) {
        // ID fields for main entity types
        registerDataFetcher(builder, OUTPUT_TYPE_STOPPLACE, ID, getNetexIdFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_PARENT_STOPPLACE, ID, getNetexIdFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_QUAY, ID, getNetexIdFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_GROUP_OF_STOPPLACES, ID, getNetexIdFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_TARIFF_ZONE, ID, getNetexIdFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_FARE_ZONE, ID, getNetexIdFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_PARKING, ID, getNetexIdFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_BOARDING_POSITION, ID, getNetexIdFetcher());

        // ImportedId fields
        registerDataFetcher(builder, OUTPUT_TYPE_STOPPLACE, IMPORTED_ID, getOriginalIdsFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_PARENT_STOPPLACE, IMPORTED_ID, getOriginalIdsFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_QUAY, IMPORTED_ID, getOriginalIdsFetcher());

        // ChangedBy fields
        registerDataFetcher(builder, OUTPUT_TYPE_STOPPLACE, CHANGED_BY, getChangedByFetcher(authorizationService));
        registerDataFetcher(builder, OUTPUT_TYPE_PARENT_STOPPLACE, CHANGED_BY, getChangedByFetcher(authorizationService));

        // Permissions fields
        registerDataFetcher(builder, OUTPUT_TYPE_STOPPLACE, PERMISSIONS, dataFetcherRegistry.getEntityPermissionsFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_PARENT_STOPPLACE, PERMISSIONS, dataFetcherRegistry.getEntityPermissionsFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_GROUP_OF_STOPPLACES, PERMISSIONS, dataFetcherRegistry.getEntityPermissionsFetcher());

        // Accessibility fields
        registerDataFetcher(builder, OUTPUT_TYPE_ACCESSIBILITY_ASSESSMENT, ID, getNetexIdFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_ACCESSIBILITY_ASSESSMENT, LIMITATIONS, getAccessibilityLimitationsFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_ACCESSIBILITY_LIMITATIONS, ID, getNetexIdFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_PURPOSE_OF_GROUPING, ID, getNetexIdFetcher());
    }

    /**
     * Registers data fetchers for entity-specific fields.
     * Includes: tags, geometry, placeEquipments, tariffZones, fareZones, stopPlaceGroups, keyValues, polygon.
     *
     * @param builder The code registry builder to register fetchers on
     */
    private void registerEntitySpecificFields(GraphQLCodeRegistry.Builder builder) {
        // Tags
        registerDataFetcher(builder, OUTPUT_TYPE_STOPPLACE, TAGS, dataFetcherRegistry.getTagFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_PARENT_STOPPLACE, TAGS, dataFetcherRegistry.getTagFetcher());

        // Geometry
        dataFetcherGeometry(builder, OUTPUT_TYPE_STOPPLACE);
        dataFetcherGeometry(builder, OUTPUT_TYPE_PARENT_STOPPLACE);
        dataFetcherGeometry(builder, OUTPUT_TYPE_QUAY);
        dataFetcherGeometry(builder, OUTPUT_TYPE_PARKING);
        dataFetcherGeometry(builder, OUTPUT_TYPE_TARIFF_ZONE);
        dataFetcherGeometry(builder, OUTPUT_TYPE_FARE_ZONE);
        dataFetcherGeometry(builder, OUTPUT_TYPE_BOARDING_POSITION);

        // Place equipments
        dataFetcherPlaceEquipments(builder, OUTPUT_TYPE_STOPPLACE);
        dataFetcherPlaceEquipments(builder, OUTPUT_TYPE_PARENT_STOPPLACE);
        dataFetcherPlaceEquipments(builder, OUTPUT_TYPE_QUAY);

        // Tariff zones
        registerDataFetcher(builder, OUTPUT_TYPE_STOPPLACE, TARIFF_ZONES, dataFetcherRegistry.getStopPlaceTariffZoneFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_PARENT_STOPPLACE, TARIFF_ZONES, dataFetcherRegistry.getStopPlaceTariffZoneFetcher());

        // Fare zones
        registerDataFetcher(builder, OUTPUT_TYPE_STOPPLACE, FARE_ZONES, dataFetcherRegistry.getStopPlaceFareZoneFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_PARENT_STOPPLACE, FARE_ZONES, dataFetcherRegistry.getStopPlaceFareZoneFetcher());

        // Stop place groups
        registerDataFetcher(builder, OUTPUT_TYPE_STOPPLACE, STOP_PLACE_GROUPS, dataFetcherRegistry.getStopPlaceGroupsFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_PARENT_STOPPLACE, STOP_PLACE_GROUPS, dataFetcherRegistry.getStopPlaceGroupsFetcher());

        // Key values
        registerDataFetcher(builder, OUTPUT_TYPE_STOPPLACE, KEY_VALUES, dataFetcherRegistry.getKeyValuesDataFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_PARENT_STOPPLACE, KEY_VALUES, dataFetcherRegistry.getKeyValuesDataFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_TARIFF_ZONE, KEY_VALUES, dataFetcherRegistry.getKeyValuesDataFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_FARE_ZONE, KEY_VALUES, dataFetcherRegistry.getKeyValuesDataFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_QUAY, KEY_VALUES, dataFetcherRegistry.getKeyValuesDataFetcher());

        // Polygon
        registerDataFetcher(builder, OUTPUT_TYPE_STOPPLACE, POLYGON, dataFetcherRegistry.getPolygonFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_PARENT_STOPPLACE, POLYGON, dataFetcherRegistry.getPolygonFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_TARIFF_ZONE, POLYGON, dataFetcherRegistry.getPolygonFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_FARE_ZONE, POLYGON, dataFetcherRegistry.getPolygonFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_QUAY, POLYGON, dataFetcherRegistry.getPolygonFetcher());
    }

    /**
     * Registers data fetchers for root-level query operations.
     * Includes: find operations for all entity types, permissions, transport modes.
     *
     * @param builder The code registry builder to register fetchers on
     */
    private void registerQueryOperations(GraphQLCodeRegistry.Builder builder) {
        // Transport modes
        registerDataFetcher(builder, STOPPLACES_REGISTER, VALID_TRANSPORT_MODES,
                env -> servicesRegistry.getTransportModeScalar().getConfiguredTransportModes().keySet());

        // Find operations
        registerDataFetcher(builder, STOPPLACES_REGISTER, FIND_STOPPLACE, dataFetcherRegistry.getStopPlaceFetcher());
        registerDataFetcher(builder, STOPPLACES_REGISTER, FIND_STOPPLACE_BY_BBOX, dataFetcherRegistry.getStopPlaceFetcher());
        registerDataFetcher(builder, STOPPLACES_REGISTER, FIND_TOPOGRAPHIC_PLACE, dataFetcherRegistry.getTopographicPlaceFetcher());
        registerDataFetcher(builder, STOPPLACES_REGISTER, FIND_PATH_LINK, dataFetcherRegistry.getPathLinkFetcher());
        registerDataFetcher(builder, STOPPLACES_REGISTER, FIND_PARKING, dataFetcherRegistry.getParkingFetcher());
        registerDataFetcher(builder, STOPPLACES_REGISTER, TAGS, dataFetcherRegistry.getTagFetcher());
        registerDataFetcher(builder, STOPPLACES_REGISTER, GROUP_OF_STOP_PLACES, dataFetcherRegistry.getGroupOfStopPlacesFetcher());
        registerDataFetcher(builder, STOPPLACES_REGISTER, PURPOSE_OF_GROUPING, dataFetcherRegistry.getPurposeOfGroupingFetcher());
        registerDataFetcher(builder, STOPPLACES_REGISTER, GROUP_OF_TARIFF_ZONES, dataFetcherRegistry.getGroupOfTariffZonesFetcher());
        registerDataFetcher(builder, STOPPLACES_REGISTER, TARIFF_ZONES, dataFetcherRegistry.getTariffZonesFetcher());
        registerDataFetcher(builder, STOPPLACES_REGISTER, FARE_ZONES, dataFetcherRegistry.getFareZonesFetcher());
        registerDataFetcher(builder, STOPPLACES_REGISTER, FARE_ZONES_AUTHORITIES, dataFetcherRegistry.getFareZoneAuthoritiesFetcher());

        // Permissions
        registerDataFetcher(builder, STOPPLACES_REGISTER, USER_PERMISSIONS, dataFetcherRegistry.getUserPermissionsFetcher());
        registerDataFetcher(builder, STOPPLACES_REGISTER, LOCATION_PERMISSIONS, dataFetcherRegistry.getLocationPermissionsFetcher());
    }

    /**
     * Registers data fetchers for specialized nested types.
     * Includes: GeoJSON, equipment, transport modes, FareZone, GroupOfStopPlaces,
     * GroupOfTariffZones, PathLink, TopographicPlace, StopPlace specific fields.
     *
     * @param builder The code registry builder to register fetchers on
     */
    private void registerSpecializedTypeFetchers(GraphQLCodeRegistry.Builder builder) {
        // GeoJSON type
        registerDataFetcher(builder, OUTPUT_TYPE_GEO_JSON, TYPE, getGeoJsonTypeFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_GEO_JSON, LEGACY_COORDINATES, getLegacyCoordinates());

        // PlaceEquipments
        registerDataFetcher(builder, OUTPUT_TYPE_PLACE_EQUIPMENTS, ID, getNetexIdFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_PLACE_EQUIPMENTS, WAITING_ROOM_EQUIPMENT,
                env -> getEquipmentOfType(WaitingRoomEquipment.class, env));
        registerDataFetcher(builder, OUTPUT_TYPE_PLACE_EQUIPMENTS, SANITARY_EQUIPMENT,
                env -> getEquipmentOfType(SanitaryEquipment.class, env));
        registerDataFetcher(builder, OUTPUT_TYPE_PLACE_EQUIPMENTS, TICKETING_EQUIPMENT,
                env -> getEquipmentOfType(TicketingEquipment.class, env));
        registerDataFetcher(builder, OUTPUT_TYPE_PLACE_EQUIPMENTS, SHELTER_EQUIPMENT,
                env -> getEquipmentOfType(ShelterEquipment.class, env));
        registerDataFetcher(builder, OUTPUT_TYPE_PLACE_EQUIPMENTS, CYCLE_STORAGE_EQUIPMENT,
                env -> getEquipmentOfType(CycleStorageEquipment.class, env));
        registerDataFetcher(builder, OUTPUT_TYPE_PLACE_EQUIPMENTS, GENERAL_SIGN,
                env -> getEquipmentOfType(GeneralSign.class, env));

        // Equipment type ID mapping
        mapNetexId(builder, OUTPUT_TYPE_SHELTER_EQUIPMENT, OUTPUT_TYPE_SANITARY_EQUIPMENT,
                OUTPUT_TYPE_CYCLE_STORAGE_EQUIPMENT, OUTPUT_TYPE_GENERAL_SIGN_EQUIPMENT,
                OUTPUT_TYPE_TICKETING_EQUIPMENT, OUTPUT_TYPE_WAITING_ROOM_EQUIPMENT);

        // BoardingPosition
        registerDataFetcher(builder, OUTPUT_TYPE_BOARDING_POSITION, ID, getNetexIdFetcher());

        // Transport modes
        registerDataFetcher(builder, "TransportModes", "transportMode", env -> env.getSource());
        registerDataFetcher(builder, "TransportModes", "submode", env -> getValidSubmodes(env.getSource()));
        registerDataFetcher(builder, OUTPUT_TYPE_STOPPLACE, SUBMODE,
                env -> servicesRegistry.getTransportModeScalar().resolveSubmode(env));

        // Parking
        registerDataFetcher(builder, OUTPUT_TYPE_PARKING, PARENT_SITE_REF, getParkingParentSiteRefFetcher());

        // StopPlace
        registerDataFetcher(builder, OUTPUT_TYPE_STOPPLACE, PARENT_SITE_REF, getStopPlaceParentSiteRefFetcher());

        // EntityRef
        registerDataFetcher(builder, OUTPUT_TYPE_ENTITY_REF, ADDRESSABLE_PLACE, dataFetcherRegistry.getReferenceFetcher());

        // FareZone
        registerDataFetcher(builder, OUTPUT_TYPE_FARE_ZONE, FARE_ZONES_AUTHORITY_REF,
                env -> env.getSource() instanceof FareZone fareZone ? fareZone.getTransportOrganisationRef() : null);
        registerDataFetcher(builder, OUTPUT_TYPE_FARE_ZONE, FARE_ZONES_NEIGHBOURS,
                env -> typeRegistry.getFareZoneTypeFactory().fareZoneNeighboursType(env));
        registerDataFetcher(builder, OUTPUT_TYPE_FARE_ZONE, FARE_ZONES_MEMBERS,
                env -> typeRegistry.getFareZoneTypeFactory().fareZoneMemberType(env));

        // GroupOfStopPlaces
        registerDataFetcher(builder, OUTPUT_TYPE_GROUP_OF_STOPPLACES, PURPOSE_OF_GROUPING,
                dataFetcherRegistry.getGroupOfStopPlacesPurposeOfGroupingFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_GROUP_OF_STOPPLACES, GROUP_OF_STOP_PLACES_MEMBERS,
                dataFetcherRegistry.getGroupOfStopPlacesMembersFetcher());

        // GroupOfTariffZones
        registerDataFetcher(builder, OUTPUT_TYPE_GROUP_OF_TARIFF_ZONES, GROUP_OF_TARIFF_ZONES_MEMBERS,
                env -> typeRegistry.getGroupOfTariffZonesTypeFactory().groupOfTariffZoneMembersType(env));
        registerDataFetcher(builder, OUTPUT_TYPE_GROUP_OF_TARIFF_ZONES, ID, getNetexIdFetcher());

        // PathLink transfer durations
        registerDataFetcher(builder, OUTPUT_TYPE_TRANSFER_DURATION, DEFAULT_DURATION,
                typeRegistry.getPathLinkTypeFactory().durationSecondsFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_TRANSFER_DURATION, FREQUENT_TRAVELLER_DURATION,
                typeRegistry.getPathLinkTypeFactory().durationSecondsFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_TRANSFER_DURATION, OCCASIONAL_TRAVELLER_DURATION,
                typeRegistry.getPathLinkTypeFactory().durationSecondsFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_TRANSFER_DURATION, MOBILITY_RESTRICTED_TRAVELLER_DURATION,
                typeRegistry.getPathLinkTypeFactory().durationSecondsFetcher());

        // TopographicPlace
        registerDataFetcher(builder, OUTPUT_TYPE_TOPOGRAPHIC_PLACE, ID, getTopographicPlaceIdFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_TOPOGRAPHIC_PLACE, PARENT_TOPOGRAPHIC_PLACE,
                getParentTopographicPlaceFetcher());
        registerDataFetcher(builder, OUTPUT_TYPE_TOPOGRAPHIC_PLACE, POLYGON, dataFetcherRegistry.getPolygonFetcher());
    }

    /**
     * Registers data fetchers for mutation operations.
     * Includes: create, update, delete, merge, terminate, reopen operations.
     *
     * @param builder The code registry builder to register fetchers on
     */
    private void registerMutationOperations(GraphQLCodeRegistry.Builder builder) {
        // Entity mutations
        registerDataFetcher(builder, STOPPLACES_MUTATION, MUTATE_PARKING, dataFetcherRegistry.getParkingUpdater());
        registerDataFetcher(builder, STOPPLACES_MUTATION, MUTATE_STOPPLACE, dataFetcherRegistry.getStopPlaceUpdater());
        registerDataFetcher(builder, STOPPLACES_MUTATION, MUTATE_PARENT_STOPPLACE, dataFetcherRegistry.getStopPlaceUpdater());
        registerDataFetcher(builder, STOPPLACES_MUTATION, MUTATE_GROUP_OF_STOP_PLACES, dataFetcherRegistry.getGroupOfStopPlacesUpdater());
        registerDataFetcher(builder, STOPPLACES_MUTATION, MUTATE_PURPOSE_OF_GROUPING, dataFetcherRegistry.getPurposeOfGroupingUpdater());
        registerDataFetcher(builder, STOPPLACES_MUTATION, MUTATE_PATH_LINK, dataFetcherRegistry.getPathLinkUpdater());

        // Terminate operations
        registerDataFetcher(builder, STOPPLACES_MUTATION, TERMINATE_TARIFF_ZONE,
                env -> servicesRegistry.getTariffZoneTerminator().terminateTariffZone(
                        env.getArgument(TARIFF_ZONE_ID),
                        env.getArgument(VALID_BETWEEN_TO_DATE),
                        env.getArgument(VERSION_COMMENT)));
        registerDataFetcher(builder, STOPPLACES_MUTATION, TERMINATE_STOP_PLACE,
                env -> servicesRegistry.getStopPlaceTerminator().terminateStopPlace(
                        env.getArgument(STOP_PLACE_ID),
                        env.getArgument(VALID_BETWEEN_TO_DATE),
                        env.getArgument(VERSION_COMMENT),
                        env.getArgument(MODIFICATION_ENUMERATION)));

        // Delete operations
        registerDataFetcher(builder, STOPPLACES_MUTATION, DELETE_GROUP_OF_STOPPLACES, dataFetcherRegistry.getGroupOfStopPlacesDeleterFetcher());
        registerDataFetcher(builder, STOPPLACES_MUTATION, DELETE_PARKING,
                env -> servicesRegistry.getParkingDeleter().deleteParking(env.getArgument(PARKING_ID)));
        registerDataFetcher(builder, STOPPLACES_MUTATION, DELETE_STOP_PLACE,
                env -> servicesRegistry.getStopPlaceDeleter().deleteStopPlace(env.getArgument(STOP_PLACE_ID)));
        registerDataFetcher(builder, STOPPLACES_MUTATION, DELETE_QUAY_FROM_STOP_PLACE,
                env -> servicesRegistry.getStopPlaceQuayDeleter().deleteQuay(
                        env.getArgument(STOP_PLACE_ID),
                        env.getArgument(QUAY_ID),
                        env.getArgument(VERSION_COMMENT)));

        // Reopen operation
        registerDataFetcher(builder, STOPPLACES_MUTATION, REOPEN_STOP_PLACE,
                env -> servicesRegistry.getStopPlaceReopener().reopenStopPlace(
                        env.getArgument(STOP_PLACE_ID),
                        env.getArgument(VERSION_COMMENT)));

        // Merge operations
        registerDataFetcher(builder, STOPPLACES_MUTATION, MERGE_STOP_PLACES,
                env -> servicesRegistry.getStopPlaceMerger().mergeStopPlaces(
                        env.getArgument(FROM_STOP_PLACE_ID),
                        env.getArgument(TO_STOP_PLACE_ID),
                        env.getArgument(FROM_VERSION_COMMENT),
                        env.getArgument(TO_VERSION_COMMENT),
                        env.getArgument(DRY_RUN)));
        registerDataFetcher(builder, STOPPLACES_MUTATION, MERGE_QUAYS,
                env -> servicesRegistry.getStopPlaceQuayMerger().mergeQuays(
                        env.getArgument(STOP_PLACE_ID),
                        env.getArgument(FROM_QUAY_ID),
                        env.getArgument(TO_QUAY_ID),
                        env.getArgument(VERSION_COMMENT),
                        env.getArgument(DRY_RUN)));

        // Move operation
        registerDataFetcher(builder, STOPPLACES_MUTATION, MOVE_QUAYS_TO_STOP,
                env -> servicesRegistry.getStopPlaceQuayMover().moveQuays(
                        env.getArgument(QUAY_IDS),
                        env.getArgument(TO_STOP_PLACE_ID),
                        env.getArgument(FROM_VERSION_COMMENT),
                        env.getArgument(TO_VERSION_COMMENT)));

        // Multi-modal operations
        registerDataFetcher(builder, STOPPLACES_MUTATION, CREATE_MULTI_MODAL_STOPPLACE, this::createMultiModalStopPlaceFetcher);
        registerDataFetcher(builder, STOPPLACES_MUTATION, ADD_TO_MULTIMODAL_STOPPLACE, this::addToMultiModalStopPlaceFetcher);
        registerDataFetcher(builder, STOPPLACES_MUTATION, REMOVE_FROM_MULTIMODAL_STOPPLACE,
                env -> servicesRegistry.getParentStopPlaceEditor().removeFromMultiModalStopPlace(
                        env.getArgument(PARENT_SITE_REF),
                        env.getArgument(STOP_PLACE_ID)));

        // Tag operations
        registerDataFetcher(builder, STOPPLACES_MUTATION, REMOVE_TAG,
                env -> servicesRegistry.getTagRemover().removeTag(
                        env.getArgument(TAG_NAME),
                        env.getArgument(TAG_ID_REFERENCE),
                        env.getArgument(TAG_COMMENT)));
        registerDataFetcher(builder, STOPPLACES_MUTATION, CREATE_TAG,
                env -> servicesRegistry.getTagCreator().createTag(
                        env.getArgument(TAG_NAME),
                        env.getArgument(TAG_ID_REFERENCE),
                        env.getArgument(TAG_COMMENT)));
    }

    /**
     * Builds the GraphQL code registry by registering all data fetchers for queries and mutations.
     *
     * The code registry maps GraphQL schema fields to their corresponding data fetchers,
     * enabling field resolution during query execution.
     *
     * Organization:
     * - Entity common fields (ID, importedId, changedBy, permissions)
     * - Entity-specific fields (tags, geometry, equipment, zones, keyValues)
     * - Query operations (find operations, permissions)
     * - Specialized type fetchers (GeoJSON, equipment, zones, places)
     * - Mutation operations (create, update, delete, merge, terminate)
     * - Type resolver for StopPlace interface
     *
     * @param stopPlaceTypeResolver Resolver for determining concrete StopPlace types
     * @return Fully configured GraphQL code registry
     */
    public GraphQLCodeRegistry buildCodeRegistry(TypeResolver stopPlaceTypeResolver) {
        GraphQLCodeRegistry.Builder codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry();

        // Register data fetchers by category
        registerEntityCommonFields(codeRegistryBuilder);
        registerEntitySpecificFields(codeRegistryBuilder);
        registerQueryOperations(codeRegistryBuilder);
        registerSpecializedTypeFetchers(codeRegistryBuilder);
        registerMutationOperations(codeRegistryBuilder);

        // Register type resolver for StopPlace interface
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
}
