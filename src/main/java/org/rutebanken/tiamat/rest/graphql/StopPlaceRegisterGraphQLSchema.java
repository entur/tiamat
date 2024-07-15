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
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.GroupOfTariffZones;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.rest.graphql.fetchers.AuthorizationCheckDataFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.FareZoneAuthoritiesFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.TagFetcher;
import org.rutebanken.tiamat.rest.graphql.operations.MultiModalityOperationsBuilder;
import org.rutebanken.tiamat.rest.graphql.operations.OrganisationOperationsBuilder;
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
import org.rutebanken.tiamat.service.TariffZoneTerminator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.GraphQLBigDecimal;
import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.AuthorizationCheckCreator.createAuthorizationCheckArguments;
import static org.rutebanken.tiamat.rest.graphql.types.AuthorizationCheckCreator.createAuthorizationCheckOutputType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.accessibilityAssessmentInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.accessibilityAssessmentObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.alternativeNameInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.alternativeNameObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.boardingPositionsInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.boardingPositionsObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.createOrganisationInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.createOrganisationObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.createParkingInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.createParkingObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultiLingualStringInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.equipmentInputType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.equipmentType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geoJsonInputType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geometryFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.interchangeWeightingEnum;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.keyValuesObjectInputType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.modificationEnumerationType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.netexIdFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.pathLinkObjectInputType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.privateCodeFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.privateCodeInputType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.scopingMethodEnumType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.stopPlaceTypeEnum;
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
    private OrganisationOperationsBuilder organisationOperationsBuilder;

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
    private AuthorizationCheckDataFetcher authorizationCheckDataFetcher;

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
    DataFetcher organisationFetcher;

    @Autowired
    DataFetcher organisationUpdater;

    @Autowired
    DateScalar dateScalar;

    @Autowired
    TransportModeScalar transportModeScalar;

    @Autowired
    FareZoneAuthoritiesFetcher fareZoneAuthoritiesFetcher;


    @PostConstruct
    public void init() {

        /**
         * Common field list for quays, stop places and addressable place.
         */
        List<GraphQLFieldDefinition> commonFieldsList = new ArrayList<>();
        commonFieldsList.add(newFieldDefinition()
                .name(PLACE_EQUIPMENTS)
                .type(equipmentType)
                .dataFetcher(env -> {
                    if (env.getSource() instanceof StopPlace stopPlace) {
                        return stopPlace.getPlaceEquipments();
                    } else if (env.getSource() instanceof Quay quay) {
                        return quay.getPlaceEquipments();
                    }
                    return null;
                })
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

        GraphQLObjectType validBetweenObjectType = createValidBetweenObjectType();

        List<GraphQLFieldDefinition> zoneCommandFieldList = zoneCommonFieldListCreator.create(validBetweenObjectType);

        commonFieldsList.addAll(zoneCommandFieldList);

        GraphQLObjectType quayObjectType = createQuayObjectType(commonFieldsList);


        GraphQLObjectType topographicPlaceObjectType = topographicPlaceObjectTypeCreator.create();

        GraphQLObjectType tariffZoneObjectType = tariffZoneObjectTypeCreator.create(zoneCommandFieldList);
        GraphQLObjectType fareZoneObjectType = fareZoneObjectTypeCreator.create(zoneCommandFieldList);

        MutableTypeResolver stopPlaceTypeResolver = new MutableTypeResolver();

        List<GraphQLFieldDefinition> stopPlaceInterfaceFields = stopPlaceInterfaceCreator.createCommonInterfaceFields(tariffZoneObjectType,fareZoneObjectType, topographicPlaceObjectType, validBetweenObjectType);
        GraphQLInterfaceType stopPlaceInterface = stopPlaceInterfaceCreator.createInterface(stopPlaceInterfaceFields, commonFieldsList, stopPlaceTypeResolver);

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
        GraphQLObjectType groupOfStopPlacesObjectType = groupOfStopPlaceObjectTypeCreator.create(stopPlaceInterface, purposeOfGroupingType, validBetweenObjectType);
        GraphQLObjectType groupOfTariffZonesObjectType = groupOfTariffZonesObjectTypeCreator.create();

        GraphQLObjectType addressablePlaceObjectType = createAddressablePlaceObjectType(commonFieldsList);

        GraphQLObjectType entityRefObjectType = entityRefObjectTypeCreator.create(addressablePlaceObjectType);

        GraphQLObjectType pathLinkEndObjectType = pathLinkEndObjectTypeCreator.create(entityRefObjectType, netexIdFieldDefinition);

        GraphQLObjectType pathLinkObjectType = pathLinkObjectTypeCreator.create(pathLinkEndObjectType, netexIdFieldDefinition, geometryFieldDefinition);

        GraphQLObjectType parkingObjectType = createParkingObjectType(validBetweenObjectType);

        GraphQLObjectType organisationObjectType = createOrganisationObjectType(validBetweenObjectType);

        GraphQLArgument allVersionsArgument = GraphQLArgument.newArgument()
                .name(ALL_VERSIONS)
                .type(GraphQLBoolean)
                .description(ALL_VERSIONS_ARG_DESCRIPTION)
                .build();

        GraphQLObjectType stopPlaceRegisterQuery = newObject()
                .name("StopPlaceRegister")
                .description("Query and search for data")
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceInterface))
                        .name(FIND_STOPPLACE)
                        .description("Search for StopPlaces")
                        .argument(createFindStopPlaceArguments(allVersionsArgument))
                        .dataFetcher(stopPlaceFetcher))
                        //Search by BoundingBox
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceInterface))
                        .name(FIND_STOPPLACE_BY_BBOX)
                        .description("Find StopPlaces within given BoundingBox.")
                        .argument(createBboxArguments())
                        .dataFetcher(stopPlaceFetcher))
                .field(newFieldDefinition()
                        .name(FIND_TOPOGRAPHIC_PLACE)
                        .type(new GraphQLList(topographicPlaceObjectType))
                        .description("Find topographic places")
                        .argument(createFindTopographicPlaceArguments(allVersionsArgument))
                        .dataFetcher(topographicPlaceFetcher))
                .field(newFieldDefinition()
                        .name(FIND_PATH_LINK)
                        .type(new GraphQLList(pathLinkObjectType))
                        .description("Find path links")
                        .argument(createFindPathLinkArguments(allVersionsArgument))
                        .dataFetcher(pathLinkFetcher))
                .field(newFieldDefinition()
                        .name(FIND_PARKING)
                        .type(new GraphQLList(parkingObjectType))
                        .description("Find parking")
                        .argument(createFindParkingArguments(allVersionsArgument))
                        .dataFetcher(parkingFetcher))
                .field(newFieldDefinition()
                        .name(FIND_ORGANISATION)
                        .type(new GraphQLList(organisationObjectType))
                        .description("Find organisation")
                        .argument(createFindOrganisationArguments(allVersionsArgument))
                        .dataFetcher(organisationFetcher))
                .field(newFieldDefinition()
                        .name(VALID_TRANSPORT_MODES)
                        .type(new GraphQLList(transportModeSubmodeObjectType))
                        .description("List all valid Transportmode/Submode-combinations.")
                        .staticValue(transportModeScalar.getConfiguredTransportModes().keySet()))
                .field(newFieldDefinition()
                        .name(CHECK_AUTHORIZED)
                        .type(createAuthorizationCheckOutputType())
                        .description(AUTHORIZATION_CHECK_DESCRIPTION)
                        .argument(createAuthorizationCheckArguments())
                        .dataFetcher(authorizationCheckDataFetcher))
                .field(newFieldDefinition()
                        .name(TAGS)
                        .type(new GraphQLList(tagObjectTypeCreator.create()))
                        .description(TAGS_DESCRIPTION)
                        .argument(GraphQLArgument.newArgument()
                            .name(TAG_NAME)
                            .description(TAG_NAME_DESCRIPTION)
                            .type(new GraphQLNonNull(GraphQLString)))
                        .dataFetcher(tagFetcher)
                        .build())
                .field(newFieldDefinition()
                        .name(GROUP_OF_STOP_PLACES)
                        .type(new GraphQLList(groupOfStopPlacesObjectType))
                        .description("Group of stop places")
                        .argument(createFindGroupOfStopPlacesArguments())
                        .dataFetcher(groupOfStopPlacesFetcher)
                        .build())
                .field(newFieldDefinition()
                        .name(PURPOSE_OF_GROUPING)
                        .type(new GraphQLList(purposeOfGroupingType))
                        .description("List all purpose of grouping")
                        .argument(createFindPurposeOfGroupingArguments())
                        .dataFetcher(purposeOfGroupingFetcher)
                )

                .field(newFieldDefinition()
                        .name(GROUP_OF_TARIFF_ZONES)
                        .type(new GraphQLList(groupOfTariffZonesObjectType))
                        .description("Group of tariff zones")
                        .argument(createFindGroupOfTariffZonesArguments())
                        .dataFetcher(groupOfTariffZonesFetcher)
                        .build())
                .field(newFieldDefinition()
                        .name(TARIFF_ZONES)
                        .type(new GraphQLList(tariffZoneObjectType))
                        .description("Tariff zones")
                        .argument(createFindTariffZonesArguments())
                        .dataFetcher(tariffZonesFetcher)
                        .build())
                .field(newFieldDefinition()
                        .name(FARE_ZONES)
                        .type(new GraphQLList(fareZoneObjectType))
                        .description("Fare zones")
                        .argument(createFindFareZonesArguments())
                        .dataFetcher(fareZonesFetcher)
                        .build())
                .field(newFieldDefinition()
                        .name(FARE_ZONES_AUTHORITIES)
                        .type(new GraphQLList(GraphQLString))
                        .description("List all fare zone authorities.")
                        .dataFetcher(fareZoneAuthoritiesFetcher)
                        .build())
                .build();


        List<GraphQLInputObjectField> commonInputFieldList = createCommonInputFieldList(embeddableMultiLingualStringInputObjectType);

        GraphQLInputObjectType quayInputObjectType = createQuayInputObjectType(commonInputFieldList);

        GraphQLInputObjectType validBetweenInputObjectType = createValidBetweenInputObjectType();

        GraphQLInputObjectType stopPlaceInputObjectType = createStopPlaceInputObjectType(commonInputFieldList,
                topographicPlaceInputObjectType, quayInputObjectType, validBetweenInputObjectType);

        GraphQLInputObjectType parentStopPlaceInputObjectType = parentStopPlaceInputObjectTypeCreator.create(commonInputFieldList, validBetweenInputObjectType, stopPlaceInputObjectType);

        GraphQLInputObjectType parkingInputObjectType = createParkingInputObjectType(validBetweenInputObjectType);

        GraphQLInputObjectType organisationInputObjectType = createOrganisationInputObjectType(validBetweenInputObjectType);

        GraphQLInputObjectType groupOfStopPlacesInputObjectType = createGroupOfStopPlacesInputObjectType(validBetweenInputObjectType);

        GraphQLInputObjectType purposeOfGroupingInputObjectType =createPurposeOfGroupingInputObjectType();

        GraphQLObjectType stopPlaceRegisterMutation = newObject()
                .name("StopPlaceMutation")
                .description("Create and edit stopplaces")
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(MUTATE_STOPPLACE)
                        .description("Create new or update existing StopPlace")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_STOPPLACE)
                                .type(stopPlaceInputObjectType))
                        .dataFetcher(stopPlaceUpdater))
                .field(newFieldDefinition()
                        .type(new GraphQLList(parentStopPlaceObjectType))
                        .name(MUTATE_PARENT_STOPPLACE)
                        .description("Update existing Parent StopPlace")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_PARENT_STOPPLACE)
                                .type(parentStopPlaceInputObjectType))
                        .dataFetcher(stopPlaceUpdater))
                .field(newFieldDefinition()
                        .name(MUTATE_GROUP_OF_STOP_PLACES)
                        .type(groupOfStopPlacesObjectType)
                        .description("Mutate group of stop places")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_GROUP_OF_STOPPLACES)
                                .type(groupOfStopPlacesInputObjectType))
                        .dataFetcher(groupOfStopPlacesUpdater))
                .field(newFieldDefinition()
                        .name(MUTATE_PURPOSE_OF_GROUPING)
                        .type(purposeOfGroupingType)
                        .description("Mutate purpose of grouping")
                        .argument(GraphQLArgument.newArgument().
                                name(OUTPUT_TYPE_PURPOSE_OF_GROUPING)
                                .type(purposeOfGroupingInputObjectType))
                        .dataFetcher(purposeOfGroupingUpdater))
                .field(newFieldDefinition()
                        .type(new GraphQLList(pathLinkObjectType))
                        .name(MUTATE_PATH_LINK)
                        .description("Create new or update existing PathLink")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_PATH_LINK)
                                .type(new GraphQLList(pathLinkObjectInputType)))
                        .description("Create new or update existing " + OUTPUT_TYPE_PATH_LINK)
                        .dataFetcher(pathLinkUpdater))
                .field(newFieldDefinition()
                        .type(new GraphQLList(parkingObjectType))
                        .name(MUTATE_PARKING)
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_PARKING)
                                .type(new GraphQLList(parkingInputObjectType)))
                        .description("Create new or update existing " + OUTPUT_TYPE_PARKING)
                        .dataFetcher(parkingUpdater))

                .field(newFieldDefinition()
                        .type(new GraphQLList(organisationObjectType))
                        .name(MUTATE_ORGANISATION)
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_ORGANISATION)
                                .type(new GraphQLList(organisationInputObjectType)))
                        .description("Create new or update existing " + OUTPUT_TYPE_ORGANISATION)
                        .dataFetcher(organisationUpdater))

                .field(newFieldDefinition()
                        .type(tariffZoneObjectType)
                        .name(TERMINATE_TARIFF_ZONE)
                        .description("TariffZone will be terminated and no longer be active after the given date.")
                        .argument(newArgument().name(TARIFF_ZONE_ID).type(new GraphQLNonNull(GraphQLString)))
                        .argument(newArgument().name(VALID_BETWEEN_TO_DATE).type(new GraphQLNonNull(dateScalar.getGraphQLDateScalar())))
                        .argument(newArgument().name(VERSION_COMMENT).type(GraphQLString))
                        .dataFetcher(environment -> tariffZoneTerminator.terminateTariffZone(environment.getArgument(TARIFF_ZONE_ID), environment.getArgument(VALID_BETWEEN_TO_DATE), environment.getArgument(VERSION_COMMENT))))
                .fields(tagOperationsBuilder.getTagOperations())
                .fields(stopPlaceOperationsBuilder.getStopPlaceOperations(stopPlaceInterface))
                .fields(parkingOperationsBuilder.getParkingOperations())
                .fields(organisationOperationsBuilder.getOrganisationOperations())
                .fields(multiModalityOperationsBuilder.getMultiModalityOperations(parentStopPlaceObjectType, validBetweenInputObjectType))
                .field(newFieldDefinition()
                        .type(GraphQLBoolean)
                        .name("deleteGroupOfStopPlaces")
                        .argument(GraphQLArgument.newArgument()
                                .name(ID)
                                .type(new GraphQLNonNull(GraphQLString)))
                        .description("Hard delete group of stop places by ID")
                        .dataFetcher(groupOfStopPlacesDeleterFetcher))
                .build();

        stopPlaceRegisterSchema = GraphQLSchema.newSchema()
                .query(stopPlaceRegisterQuery)
                .mutation(stopPlaceRegisterMutation)
                .build();
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

    private List<GraphQLArgument> createFindOrganisationArguments(GraphQLArgument allVersionsArgument) {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(VERSION)
                .type(GraphQLInt)
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
                .defaultValue(Boolean.FALSE)
                .description(WITHOUT_LOCATION_ONLY_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(WITHOUT_QUAYS_ONLY)
                .type(GraphQLBoolean)
                .defaultValue(Boolean.FALSE)
                .description(WITHOUT_QUAYS_ONLY_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(WITH_DUPLICATED_QUAY_IMPORTED_IDS)
                .type(GraphQLBoolean)
                .defaultValue(Boolean.FALSE)
                .description(WITH_DUPLICATED_QUAY_IMPORTED_IDS_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(WITH_NEARBY_SIMILAR_DUPLICATES)
                .type(GraphQLBoolean)
                .defaultValue(Boolean.FALSE)
                .description(WITH_NEARBY_SIMILAR_DUPLICATES_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(HAS_PARKING)
                .type(GraphQLBoolean)
                .defaultValue(Boolean.FALSE)
                .description(HAS_PARKING)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(ONLY_MONOMODAL_STOPPLACES)
                .type(GraphQLBoolean)
                .defaultValue(Boolean.FALSE)
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
                .defaultValue(Boolean.FALSE)
                .description(WITH_TAGS_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(SEARCH_WITH_CODE_SPACE)
                .type(GraphQLString)
                .defaultValue(null)
                .description(SEARCH_WITH_CODE_SPACE_ARG_DESCRIPTION)
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
                .defaultValue(Boolean.FALSE)
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
                .defaultValue(DEFAULT_PAGE_VALUE)
                .description(PAGE_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(SIZE)
                .type(GraphQLInt)
                .defaultValue(DEFAULT_SIZE_VALUE)
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
                .build();
    }

    private GraphQLInputObjectType createGroupOfStopPlacesInputObjectType(GraphQLInputObjectType validBetweenInputObjectType) {
        return newInputObject()
                .name(INPUT_TYPE_GROUP_OF_STOPPLACES)
                .field(newInputObjectField().name(ID).type(GraphQLString).description("Ignore ID when creating new"))
                .field(newInputObjectField().name(NAME).type(new GraphQLNonNull(embeddableMultiLingualStringInputObjectType)))
                .field(newInputObjectField().name(SHORT_NAME).type(embeddableMultiLingualStringInputObjectType))
                .field(newInputObjectField().name(DESCRIPTION).type(embeddableMultiLingualStringInputObjectType))
                .field(newInputObjectField().name(ALTERNATIVE_NAMES).type(new GraphQLList(alternativeNameInputObjectType)))
                .field(newInputObjectField().name(VERSION_COMMENT).type(GraphQLString))
                .field(newInputObjectField().name(PURPOSE_OF_GROUPING).type(versionLessRefInputObjectType).description("References to purpose of grouping"))
                .field(newInputObjectField().name(VALID_BETWEEN).type(validBetweenInputObjectType))
                .field(newInputObjectField().name(GEOMETRY).type(geoJsonInputType).build())
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

