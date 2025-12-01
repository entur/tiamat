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
import graphql.schema.TypeResolver;
import jakarta.annotation.PostConstruct;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.AssistanceService;
import org.rutebanken.tiamat.model.CycleStorageEquipment;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.GeneralSign;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.GroupOfTariffZones;
import org.rutebanken.tiamat.model.Link;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PostalAddress;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SanitaryEquipment;
import org.rutebanken.tiamat.model.ShelterEquipment;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TicketingEquipment;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.model.WaitingRoomEquipment;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.fetchers.EntityPermissionsFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.FareZoneAuthoritiesFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.GroupOfStopPlacesMembersFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.GroupOfStopPlacesPurposeOfGroupingFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.KeyValuesDataFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.LocationPermissionsFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.PolygonFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.StopPlaceFareZoneFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.StopPlaceTariffZoneFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.TagFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.UserPermissionsFetcher;
import org.rutebanken.tiamat.rest.graphql.mappers.GeometryMapper;
import org.rutebanken.tiamat.rest.graphql.mappers.PostalAddressMapper;
import org.rutebanken.tiamat.rest.graphql.mappers.ValidBetweenMapper;
import org.rutebanken.tiamat.rest.graphql.operations.MultiModalityOperationsBuilder;
import org.rutebanken.tiamat.rest.graphql.operations.ParkingOperationsBuilder;
import org.rutebanken.tiamat.rest.graphql.operations.StopPlaceOperationsBuilder;
import org.rutebanken.tiamat.rest.graphql.operations.TagOperationsBuilder;
import org.rutebanken.tiamat.rest.graphql.resolvers.MutableTypeResolver;
import org.rutebanken.tiamat.rest.graphql.scalars.DateScalar;
import org.rutebanken.tiamat.rest.graphql.scalars.TransportModeScalar;
import org.rutebanken.tiamat.rest.graphql.types.EntityRefObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.FareZoneObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.GroupOfStopPlacesObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.GroupOfTariffZonesObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.ParentStopPlaceInputObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.ParentStopPlaceObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.PathLinkEndObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.PathLinkObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.PurposeOfGroupingTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.StopPlaceInterfaceCreator;
import org.rutebanken.tiamat.rest.graphql.types.StopPlaceObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.TagObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.TariffZoneObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.TopographicPlaceObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.ZoneCommonFieldListCreator;
import org.rutebanken.tiamat.service.TagCreator;
import org.rutebanken.tiamat.service.TagRemover;
import org.rutebanken.tiamat.service.TariffZoneTerminator;
import org.rutebanken.tiamat.service.parking.ParkingDeleter;
import org.rutebanken.tiamat.service.stopplace.MultiModalStopPlaceEditor;
import org.rutebanken.tiamat.service.stopplace.StopPlaceDeleter;
import org.rutebanken.tiamat.service.stopplace.StopPlaceMerger;
import org.rutebanken.tiamat.service.stopplace.StopPlaceQuayDeleter;
import org.rutebanken.tiamat.service.stopplace.StopPlaceQuayMerger;
import org.rutebanken.tiamat.service.stopplace.StopPlaceQuayMover;
import org.rutebanken.tiamat.service.stopplace.StopPlaceReopener;
import org.rutebanken.tiamat.service.stopplace.StopPlaceTerminator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.accessibilityAssessmentInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.accessibilityAssessmentObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.alternativeNameInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.alternativeNameObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.boardingPositionsInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.boardingPositionsObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.createParkingInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.createParkingObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultiLingualStringInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.equipmentInputType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.equipmentType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geoJsonInputType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geometryFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.getEquipmentOfType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.getLocalServiceOfType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.interchangeWeightingEnum;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.keyValuesObjectInputType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.localServiceInputType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.localServiceType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.modificationEnumerationType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.netexIdFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.pathLinkObjectInputType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.postalAddressInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.privateCodeFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.privateCodeInputType;
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

    @Autowired
    private PathLinkEndObjectTypeCreator pathLinkEndObjectTypeCreator;

    @Autowired
    private PathLinkObjectTypeCreator pathLinkObjectTypeCreator;

    @Autowired
    private EntityRefObjectTypeCreator entityRefObjectTypeCreator;

    @Autowired
    private TopographicPlaceObjectTypeCreator topographicPlaceObjectTypeCreator;

    @Autowired
    private StopPlaceOperationsBuilder stopPlaceOperationsBuilder;

    @Autowired
    private ParkingOperationsBuilder parkingOperationsBuilder;

    @Autowired
    private ZoneCommonFieldListCreator zoneCommonFieldListCreator;

    @Autowired
    private StopPlaceObjectTypeCreator stopPlaceObjectTypeCreator;

    @Autowired
    private GroupOfStopPlacesObjectTypeCreator groupOfStopPlaceObjectTypeCreator;

    @Autowired
    private GroupOfTariffZonesObjectTypeCreator groupOfTariffZonesObjectTypeCreator;
    @Autowired
    private PurposeOfGroupingTypeCreator purposeOfGroupingTypeCreator;

    @Autowired
    private ParentStopPlaceObjectTypeCreator parentStopPlaceObjectTypeCreator;

    @Autowired
    private StopPlaceInterfaceCreator stopPlaceInterfaceCreator;

    @Autowired
    private ParentStopPlaceInputObjectTypeCreator parentStopPlaceInputObjectTypeCreator;

    @Autowired
    private TagOperationsBuilder tagOperationsBuilder;

    @Autowired
    private TagObjectTypeCreator tagObjectTypeCreator;

    @Autowired
    private TagFetcher tagFetcher;

    @Autowired
    private TariffZoneObjectTypeCreator tariffZoneObjectTypeCreator;

    @Autowired
    private FareZoneObjectTypeCreator fareZoneObjectTypeCreator;

    @Autowired
    private MultiModalityOperationsBuilder multiModalityOperationsBuilder;

    @Autowired
    DataFetcher stopPlaceFetcher;

    @Autowired
    private DataFetcher<Page<GroupOfStopPlaces>> groupOfStopPlacesFetcher;

    @Autowired
    private DataFetcher<Page<PurposeOfGrouping>> purposeOfGroupingFetcher;

    @Autowired

    private DataFetcher<Page<GroupOfTariffZones>> groupOfTariffZonesFetcher;

    @Autowired
    private DataFetcher<GroupOfStopPlaces> groupOfStopPlacesUpdater;
    @Autowired
    private DataFetcher<PurposeOfGrouping> purposeOfGroupingUpdater;

    @Autowired
    private DataFetcher<Boolean> groupOfStopPlacesDeleterFetcher;

    @Autowired
    private DataFetcher<Page<TariffZone>> tariffZonesFetcher;

    @Autowired
    private StopPlaceTariffZoneFetcher stopPlaceTariffZoneFetcher;

    @Autowired
    private EntityPermissionsFetcher entityPermissionsFetcher;

    @Autowired
    private LocationPermissionsFetcher locationPermissionsFetcher;

    @Autowired
    private StopPlaceFareZoneFetcher stopPlaceFareZoneFetcher;

    @Autowired
    private DataFetcher<Page<FareZone>> fareZonesFetcher;

    @Autowired
    TariffZoneTerminator tariffZoneTerminator;

    @Autowired
    DataFetcher pathLinkFetcher;

    @Autowired
    DataFetcher pathLinkUpdater;

    @Autowired
    DataFetcher topographicPlaceFetcher;

    @Autowired
    DataFetcher stopPlaceUpdater;

    @Autowired
    DataFetcher parkingFetcher;

    @Autowired
    DataFetcher parkingUpdater;

    @Autowired
    DateScalar dateScalar;

    @Autowired
    TransportModeScalar transportModeScalar;

    @Autowired
    FareZoneAuthoritiesFetcher fareZoneAuthoritiesFetcher;

    @Autowired
    private DataFetcher<List<GroupOfStopPlaces>> stopPlaceGroupsFetcher;

    @Autowired
    private KeyValuesDataFetcher keyValuesDataFetcher;

    @Autowired
    private PolygonFetcher polygonFetcher;

    @Autowired
    private ValidBetweenMapper validBetweenMapper;

    @Autowired
    private GeometryMapper geometryMapper;

    @Autowired
    private MultiModalStopPlaceEditor parentStopPlaceEditor;

    @Autowired
    private ParkingDeleter parkingDeleter;

    @Autowired
    private DataFetcher referenceFetcher;

    @Autowired
    private GroupOfStopPlacesMembersFetcher groupOfStopPlacesMembersFetcher;

    @Autowired
    private GroupOfStopPlacesPurposeOfGroupingFetcher groupOfStopPlacesPurposeOfGroupingFetcher;

    @Autowired
    private StopPlaceMerger stopPlaceMerger;

    @Autowired
    private StopPlaceQuayMover stopPlaceQuayMover;

    @Autowired
    private StopPlaceQuayMerger stopPlaceQuayMerger;

    @Autowired
    private StopPlaceQuayDeleter stopPlaceQuayDeleter;

    @Autowired
    private StopPlaceDeleter stopPlaceDeleter;

    @Autowired
    private StopPlaceTerminator stopPlaceTerminator;

    @Autowired
    private StopPlaceReopener stopPlaceReopener;

    @Autowired
    private TagRemover tagRemover;

    @Autowired
    private TagCreator tagCreator;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private UserPermissionsFetcher userPermissionsFetcher;


    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private PostalAddressMapper postalAddressMapper;

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
                .name(LOCAL_SERVICES)
                .type(localServiceType)
                .build());

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

        GraphQLObjectType validBetweenObjectType = createValidBetweenObjectType();
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

        GraphQLObjectType quayObjectType = createQuayObjectType(commonFieldsList);


        GraphQLObjectType topographicPlaceObjectType = topographicPlaceObjectTypeCreator.create();

        GraphQLObjectType tariffZoneObjectType = tariffZoneObjectTypeCreator.create(zoneCommandFieldList);
        GraphQLObjectType fareZoneObjectType = fareZoneObjectTypeCreator.create(zoneCommandFieldList);

        MutableTypeResolver stopPlaceTypeResolver = new MutableTypeResolver();

        List<GraphQLFieldDefinition> stopPlaceInterfaceFields = stopPlaceInterfaceCreator.createCommonInterfaceFields(tariffZoneObjectType,fareZoneObjectType, topographicPlaceObjectType, validBetweenObjectType, entityPermissionObjectType);
        GraphQLInterfaceType stopPlaceInterface = stopPlaceInterfaceCreator.createInterface(stopPlaceInterfaceFields, commonFieldsList);

        GraphQLObjectType stopPlaceObjectType = stopPlaceObjectTypeCreator.create(stopPlaceInterface, stopPlaceInterfaceFields, commonFieldsList, quayObjectType);
        GraphQLObjectType parentStopPlaceObjectType = parentStopPlaceObjectTypeCreator.create(stopPlaceInterface, stopPlaceInterfaceFields, commonFieldsList, stopPlaceObjectType);

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

        GraphQLObjectType purposeOfGroupingType =purposeOfGroupingTypeCreator.create();
        GraphQLObjectType groupOfStopPlacesObjectType = groupOfStopPlaceObjectTypeCreator.create(stopPlaceInterface, purposeOfGroupingType, entityPermissionObjectType);
        GraphQLObjectType groupOfTariffZonesObjectType = groupOfTariffZonesObjectTypeCreator.create();

        GraphQLObjectType addressablePlaceObjectType = createAddressablePlaceObjectType(commonFieldsList);

        GraphQLObjectType entityRefObjectType = entityRefObjectTypeCreator.create(addressablePlaceObjectType);

        GraphQLObjectType pathLinkEndObjectType = pathLinkEndObjectTypeCreator.create(entityRefObjectType, netexIdFieldDefinition);

        GraphQLObjectType pathLinkObjectType = pathLinkObjectTypeCreator.create(pathLinkEndObjectType, netexIdFieldDefinition, geometryFieldDefinition);

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
                        .type(new GraphQLList(tagObjectTypeCreator.create()))
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


        List<GraphQLInputObjectField> commonInputFieldList = createCommonInputFieldList(embeddableMultiLingualStringInputObjectType);

        GraphQLInputObjectType quayInputObjectType = createQuayInputObjectType(commonInputFieldList);

        GraphQLInputObjectType validBetweenInputObjectType = createValidBetweenInputObjectType();

        GraphQLInputObjectType stopPlaceInputObjectType = createStopPlaceInputObjectType(commonInputFieldList,
                topographicPlaceInputObjectType, quayInputObjectType, validBetweenInputObjectType);

        GraphQLInputObjectType parentStopPlaceInputObjectType = parentStopPlaceInputObjectTypeCreator.create(commonInputFieldList, validBetweenInputObjectType, stopPlaceInputObjectType);

        GraphQLInputObjectType parkingInputObjectType = createParkingInputObjectType(validBetweenInputObjectType);

        GraphQLInputObjectType groupOfStopPlacesInputObjectType = createGroupOfStopPlacesInputObjectType();

        GraphQLInputObjectType purposeOfGroupingInputObjectType =createPurposeOfGroupingInputObjectType();

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
                        .argument(newArgument().name(VALID_BETWEEN_TO_DATE).type(new GraphQLNonNull(dateScalar.getGraphQLDateScalar())))
                        .argument(newArgument().name(VERSION_COMMENT).type(GraphQLString))
                        )
                .fields(tagOperationsBuilder.getTagOperations())
                .fields(stopPlaceOperationsBuilder.getStopPlaceOperations(stopPlaceInterface))
                .fields(parkingOperationsBuilder.getParkingOperations())
                .fields(multiModalityOperationsBuilder.getMultiModalityOperations(parentStopPlaceObjectType, validBetweenInputObjectType))
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

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, PERMISSIONS, entityPermissionsFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, PERMISSIONS, entityPermissionsFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_GROUP_OF_STOPPLACES, PERMISSIONS, entityPermissionsFetcher);

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, TARIFF_ZONES, stopPlaceTariffZoneFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, TARIFF_ZONES, stopPlaceTariffZoneFetcher);

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



        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, TAGS, tagFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, TAGS, tagFetcher);

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


        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, STOP_PLACE_GROUPS, stopPlaceGroupsFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, STOP_PLACE_GROUPS, stopPlaceGroupsFetcher);


        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, FARE_ZONES, stopPlaceFareZoneFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, FARE_ZONES, stopPlaceFareZoneFetcher);

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, ID, getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_QUAY, ID, getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_GROUP_OF_STOPPLACES,ID,getNetexIdFetcher());

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TARIFF_ZONE,ID,getNetexIdFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_FARE_ZONE,ID,getNetexIdFetcher());

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_PARKING,ID,getNetexIdFetcher());

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, KEY_VALUES, keyValuesDataFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, KEY_VALUES, keyValuesDataFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_TARIFF_ZONE, KEY_VALUES, keyValuesDataFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_FARE_ZONE, KEY_VALUES, keyValuesDataFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_QUAY, KEY_VALUES, keyValuesDataFetcher);

        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_STOPPLACE, POLYGON, polygonFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_PARENT_STOPPLACE, POLYGON, polygonFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_TARIFF_ZONE, POLYGON, polygonFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_FARE_ZONE, POLYGON, polygonFetcher);
        registerDataFetcher(codeRegistryBuilder, OUTPUT_TYPE_QUAY, POLYGON, polygonFetcher);

        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, VALID_TRANSPORT_MODES, env -> transportModeScalar.getConfiguredTransportModes().keySet());

        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FIND_STOPPLACE, stopPlaceFetcher);
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FIND_STOPPLACE_BY_BBOX, stopPlaceFetcher);
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FIND_TOPOGRAPHIC_PLACE, topographicPlaceFetcher);
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FIND_PATH_LINK, pathLinkFetcher);
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FIND_PARKING, parkingFetcher);
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, TAGS, tagFetcher);
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, GROUP_OF_STOP_PLACES, groupOfStopPlacesFetcher);
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, PURPOSE_OF_GROUPING, purposeOfGroupingFetcher);
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, GROUP_OF_TARIFF_ZONES, groupOfTariffZonesFetcher);
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, TARIFF_ZONES, tariffZonesFetcher);
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FARE_ZONES, fareZonesFetcher);
        registerDataFetcher(codeRegistryBuilder, STOPPLACES_REGISTER, FARE_ZONES_AUTHORITIES, fareZoneAuthoritiesFetcher);


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

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_ENTITY_REF,ADDRESSABLE_PLACE,referenceFetcher);

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_FARE_ZONE,FARE_ZONES_AUTHORITY_REF,env -> env.getSource() instanceof FareZone fareZone ? fareZone.getTransportOrganisationRef() : null);
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_FARE_ZONE,FARE_ZONES_NEIGHBOURS,env -> fareZoneObjectTypeCreator.fareZoneNeighboursType(env));
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_FARE_ZONE,FARE_ZONES_MEMBERS,env -> fareZoneObjectTypeCreator.fareZoneMemberType(env));

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_GROUP_OF_STOPPLACES,PURPOSE_OF_GROUPING,groupOfStopPlacesPurposeOfGroupingFetcher);
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_GROUP_OF_STOPPLACES,GROUP_OF_STOP_PLACES_MEMBERS,groupOfStopPlacesMembersFetcher);

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_GROUP_OF_TARIFF_ZONES,GROUP_OF_TARIFF_ZONES_MEMBERS,env -> groupOfTariffZonesObjectTypeCreator.groupOfTariffZoneMembersType(env));
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_GROUP_OF_TARIFF_ZONES,ID,getNetexIdFetcher());

        //path link data fetchers
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TRANSFER_DURATION,DEFAULT_DURATION,pathLinkObjectTypeCreator.durationSecondsFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TRANSFER_DURATION,FREQUENT_TRAVELLER_DURATION,pathLinkObjectTypeCreator.durationSecondsFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TRANSFER_DURATION,OCCASIONAL_TRAVELLER_DURATION,pathLinkObjectTypeCreator.durationSecondsFetcher());
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TRANSFER_DURATION,MOBILITY_RESTRICTED_TRAVELLER_DURATION,pathLinkObjectTypeCreator.durationSecondsFetcher());

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
                    return topographicPlaceRepository.findFirstByNetexIdAndVersion(topographicPlace.getParentTopographicPlaceRef().getRef(), Long.parseLong(topographicPlace.getParentTopographicPlaceRef().getVersion()));
                }
            }
            return null;
        });
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_TOPOGRAPHIC_PLACE,POLYGON,polygonFetcher);

        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_STOPPLACE,SUBMODE,env -> transportModeScalar.resolveSubmode(env));
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_STOPPLACE,PARENT_SITE_REF,env -> {
            SiteRefStructure parentSiteRef = ((StopPlace) env.getSource()).getParentSiteRef();
            if (parentSiteRef != null) {
                return parentSiteRef.getRef();
            }
            return null;
        });

        registerDataFetcher(codeRegistryBuilder,STOPPLACES_REGISTER,USER_PERMISSIONS,userPermissionsFetcher);
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_REGISTER,LOCATION_PERMISSIONS,locationPermissionsFetcher);

        // Local services, assistance service
        registerDataFetcher(codeRegistryBuilder,OUTPUT_TYPE_LOCAL_SERVICES,ASSISTANCE_SERVICE,env -> getLocalServiceOfType(AssistanceService.class, env));
        mapNetexId(codeRegistryBuilder, OUTPUT_TYPE_ASSISTANCE_SERVICE);


        //mutation

        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MUTATE_PARKING,parkingUpdater);
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MUTATE_STOPPLACE,stopPlaceUpdater);
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MUTATE_PARENT_STOPPLACE,stopPlaceUpdater);
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MUTATE_GROUP_OF_STOP_PLACES,groupOfStopPlacesUpdater);
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MUTATE_PURPOSE_OF_GROUPING,purposeOfGroupingUpdater);
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MUTATE_PATH_LINK,pathLinkUpdater);
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,TERMINATE_TARIFF_ZONE, env -> tariffZoneTerminator.terminateTariffZone(env.getArgument(TARIFF_ZONE_ID), env.getArgument(VALID_BETWEEN_TO_DATE), env.getArgument(VERSION_COMMENT)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,DELETE_GROUP_OF_STOPPLACES,groupOfStopPlacesDeleterFetcher);
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,CREATE_MULTI_MODAL_STOPPLACE,
                environment -> {
                    Map input = environment.getArgument("input");

                    if(input == null) {
                        throw new IllegalArgumentException("input is not specified");
                    }

                    ValidBetween validBetween = validBetweenMapper.map((Map) input.get(VALID_BETWEEN));
                    String versionComment = (String) input.get(VERSION_COMMENT);
                    Point geoJsonPoint = geometryMapper.createGeoJsonPoint((Map) input.get(GEOMETRY));
                    EmbeddableMultilingualString name = getEmbeddableString((Map) input.get(NAME));
                    PostalAddress postalAddress = postalAddressMapper.map((Map) input.get(POSTAL_ADDRESS));
                    String url = (String) input.get(URL);

                    @SuppressWarnings("unchecked")
                    List<String> stopPlaceIds = (List<String>) input.get(STOP_PLACE_IDS);

                    return parentStopPlaceEditor.createMultiModalParentStopPlace(stopPlaceIds, name, validBetween, versionComment, geoJsonPoint, postalAddress, url);
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

                    ValidBetween validBetween = validBetweenMapper.map((Map) input.get(VALID_BETWEEN));
                    String versionComment = (String) input.get(VERSION_COMMENT);

                    if(input.get(STOP_PLACE_IDS) == null) {
                        throw new IllegalArgumentException("List of " + STOP_PLACE_IDS + "cannot be null");
                    }
                    @SuppressWarnings("unchecked")
                    List<String> stopPlaceIds = (List<String>) input.get(STOP_PLACE_IDS);

                    return parentStopPlaceEditor.addToMultiModalParentStopPlace(parentSiteRef, stopPlaceIds, validBetween, versionComment);
                }
        );
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,REMOVE_FROM_MULTIMODAL_STOPPLACE,
                environment -> parentStopPlaceEditor.removeFromMultiModalStopPlace(environment.getArgument(PARENT_SITE_REF), environment.getArgument(STOP_PLACE_ID))
        );


        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,DELETE_PARKING, environment -> parkingDeleter.deleteParking(environment.getArgument(PARKING_ID)));


        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MERGE_STOP_PLACES,environment -> stopPlaceMerger.mergeStopPlaces(environment.getArgument(FROM_STOP_PLACE_ID), environment.getArgument(TO_STOP_PLACE_ID), environment.getArgument(FROM_VERSION_COMMENT), environment.getArgument(TO_VERSION_COMMENT), environment.getArgument(DRY_RUN)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MERGE_QUAYS,environment -> stopPlaceQuayMerger.mergeQuays(environment.getArgument(STOP_PLACE_ID), environment.getArgument(FROM_QUAY_ID), environment.getArgument(TO_QUAY_ID), environment.getArgument(VERSION_COMMENT), environment.getArgument(DRY_RUN)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,MOVE_QUAYS_TO_STOP,environment -> stopPlaceQuayMover.moveQuays(environment.getArgument(QUAY_IDS), environment.getArgument(TO_STOP_PLACE_ID), environment.getArgument(FROM_VERSION_COMMENT), environment.getArgument(TO_VERSION_COMMENT)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,DELETE_STOP_PLACE,environment -> stopPlaceDeleter.deleteStopPlace(environment.getArgument(STOP_PLACE_ID)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,TERMINATE_STOP_PLACE,environment -> stopPlaceTerminator.terminateStopPlace(environment.getArgument(STOP_PLACE_ID), environment.getArgument(VALID_BETWEEN_TO_DATE), environment.getArgument(VERSION_COMMENT) , environment.getArgument(MODIFICATION_ENUMERATION)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,REOPEN_STOP_PLACE,environment -> stopPlaceReopener.reopenStopPlace(environment.getArgument(STOP_PLACE_ID), environment.getArgument(VERSION_COMMENT)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,DELETE_QUAY_FROM_STOP_PLACE,environment -> stopPlaceQuayDeleter.deleteQuay(environment.getArgument(STOP_PLACE_ID), environment.getArgument(QUAY_ID), environment.getArgument(VERSION_COMMENT)));

        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,REMOVE_TAG,environment -> tagRemover.removeTag(environment.getArgument(TAG_NAME), environment.getArgument(TAG_ID_REFERENCE), environment.getArgument(TAG_COMMENT)));
        registerDataFetcher(codeRegistryBuilder,STOPPLACES_MUTATION,CREATE_TAG,environment -> tagCreator.createTag(environment.getArgument(TAG_NAME), environment.getArgument(TAG_ID_REFERENCE), environment.getArgument(TAG_COMMENT)));

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

    private void mapNetexId(GraphQLCodeRegistry.Builder codeRegistryBuilder, String outputTypeAssistanceService) {
        registerDataFetcher(codeRegistryBuilder, outputTypeAssistanceService,ID,getNetexIdFetcher());
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

    private GraphQLObjectType createAddressablePlaceObjectType(List<GraphQLFieldDefinition> commonFieldsList) {
        return newObject()
                .name(OUTPUT_TYPE_ADDRESSABLE_PLACE)
                .fields(commonFieldsList)
                .build();
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
                .type(dateScalar.getGraphQLDateScalar())
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
                .type(dateScalar.getGraphQLDateScalar())
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

    private GraphQLObjectType createQuayObjectType(List<GraphQLFieldDefinition> commonFieldsList) {
        return newObject()
                    .name(OUTPUT_TYPE_QUAY)
                    .fields(commonFieldsList)
                    .field(newFieldDefinition()
                            .name(COMPASS_BEARING)
                            .type(GraphQLBigDecimal))
                    .field(newFieldDefinition()
                            .name(ALTERNATIVE_NAMES)
                            .type(new GraphQLList(alternativeNameObjectType)))
                    .field(newFieldDefinition()
                            .name(BOARDING_POSITIONS)
                            .type(new GraphQLList(boardingPositionsObjectType))
                    )
                    .build();
    }

    private GraphQLObjectType createValidBetweenObjectType() {
        return newObject()
                .name(OUTPUT_TYPE_VALID_BETWEEN)
                .field(newFieldDefinition()
                        .name(VALID_BETWEEN_FROM_DATE)
                        .type(dateScalar.getGraphQLDateScalar())
                        .description(DATE_SCALAR_DESCRIPTION))
                .field(newFieldDefinition()
                        .name(VALID_BETWEEN_TO_DATE)
                        .type(dateScalar.getGraphQLDateScalar())
                        .description(DATE_SCALAR_DESCRIPTION))
                .build();
    }

    private GraphQLInputObjectType createValidBetweenInputObjectType() {
        return newInputObject()
                .name(INPUT_TYPE_VALID_BETWEEN)
                .field(newInputObjectField()
                        .name(VALID_BETWEEN_FROM_DATE)
                        .type(new GraphQLNonNull(dateScalar.getGraphQLDateScalar()))
                        .description("When the new version is valid from"))
                .field(newInputObjectField()
                        .name(VALID_BETWEEN_TO_DATE)
                        .type(dateScalar.getGraphQLDateScalar())
                        .description("When the version is no longer valid"))
                .build();
    }


    private GraphQLInputObjectType createStopPlaceInputObjectType(List<GraphQLInputObjectField> commonInputFieldsList,
                                                                  GraphQLInputObjectType topographicPlaceInputObjectType,
                                                                  GraphQLInputObjectType quayObjectInputType,
                                                                  GraphQLInputObjectType validBetweenInputObjectType) {
        return newInputObject()
                .name(INPUT_TYPE_STOPPLACE)
                .fields(commonInputFieldsList)
                .fields(transportModeScalar.createTransportModeInputFieldsList())
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
                .field(newInputObjectField()
                        .name(POSTAL_ADDRESS)
                        .type(postalAddressInputObjectType).build())
                .build();
    }

    private GraphQLInputObjectType createGroupOfStopPlacesInputObjectType() {
        return newInputObject()
                .name(INPUT_TYPE_GROUP_OF_STOPPLACES)
                .field(newInputObjectField().name(ID).type(GraphQLString).description("Ignore ID when creating new"))
                .field(newInputObjectField().name(NAME).type(new GraphQLNonNull(embeddableMultiLingualStringInputObjectType)))
                .field(newInputObjectField().name(SHORT_NAME).type(embeddableMultiLingualStringInputObjectType))
                .field(newInputObjectField().name(DESCRIPTION).type(embeddableMultiLingualStringInputObjectType))
                .field(newInputObjectField().name(ALTERNATIVE_NAMES).type(new GraphQLList(alternativeNameInputObjectType)))
                .field(newInputObjectField().name(VERSION_COMMENT).type(GraphQLString))
                .field(newInputObjectField().name(PURPOSE_OF_GROUPING).type(versionLessRefInputObjectType).description("References to purpose of grouping"))
                .field(newInputObjectField()
                        .name(GROUP_OF_STOP_PLACES_MEMBERS)
                        .description("References to group of stop places members. Stop place IDs.")
                        .type(new GraphQLList(versionLessRefInputObjectType)))
                .build();
    }

    private GraphQLInputObjectType createPurposeOfGroupingInputObjectType() {
        return newInputObject()
                .name(INPUT_TYPE_PURPOSE_OF_GROUPING)
                .field(newInputObjectField().name(ID).type(GraphQLString).description("Ignore ID when creating new"))
                .field(newInputObjectField().name(NAME).type(new GraphQLNonNull(embeddableMultiLingualStringInputObjectType)))
                .field(newInputObjectField().name(DESCRIPTION).type(embeddableMultiLingualStringInputObjectType))
                .field(newInputObjectField().name(VERSION_COMMENT).type(GraphQLString))
                .build();

    }

    private List<GraphQLInputObjectField> createCommonInputFieldList(GraphQLInputObjectType embeddableMultiLingualStringInputObjectType) {

        List<GraphQLInputObjectField> commonInputFieldsList = new ArrayList<>();
        commonInputFieldsList.add(newInputObjectField().name(ID).type(GraphQLString).description("Ignore when creating new").build());
        commonInputFieldsList.add(newInputObjectField().name(NAME).type(embeddableMultiLingualStringInputObjectType).build());
        commonInputFieldsList.add(newInputObjectField().name(SHORT_NAME).type(embeddableMultiLingualStringInputObjectType).build());
        commonInputFieldsList.add(newInputObjectField().name(PUBLIC_CODE).type(GraphQLString).build());
        commonInputFieldsList.add(newInputObjectField().name(PRIVATE_CODE).type(privateCodeInputType).build());
        commonInputFieldsList.add(newInputObjectField().name(DESCRIPTION).type(embeddableMultiLingualStringInputObjectType).build());
        commonInputFieldsList.add(newInputObjectField().name(GEOMETRY).type(geoJsonInputType).build());
        commonInputFieldsList.add(newInputObjectField().name(ALTERNATIVE_NAMES).type(new GraphQLList(alternativeNameInputObjectType)).build());
        commonInputFieldsList.add(newInputObjectField().name(PLACE_EQUIPMENTS).type(equipmentInputType).build());
        commonInputFieldsList.add(newInputObjectField().name(LOCAL_SERVICES).type(localServiceInputType).build());
        commonInputFieldsList.add(newInputObjectField().name(KEY_VALUES).type(new GraphQLList(keyValuesObjectInputType)).build());
        commonInputFieldsList.add(
                newInputObjectField()
                        .name(ACCESSIBILITY_ASSESSMENT)
                        .description("This field is set either on StopPlace (i.e. all Quays are equal), or on every Quay.")
                        .type(accessibilityAssessmentInputObjectType)
                        .build()
        );
        return commonInputFieldsList;
    }


    private GraphQLInputObjectType createQuayInputObjectType(List<GraphQLInputObjectField> graphQLCommonInputObjectFieldsList) {
        return newInputObject()
                .name(INPUT_TYPE_QUAY)
                .fields(graphQLCommonInputObjectFieldsList)
                .field(newInputObjectField()
                        .name(COMPASS_BEARING)
                        .type(GraphQLBigDecimal))
                .field(newInputObjectField()
                .name(BOARDING_POSITIONS)
                .type(new GraphQLList(boardingPositionsInputObjectType)))
                .build();
    }

}

