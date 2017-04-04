package org.rutebanken.tiamat.rest.graphql;

import graphql.schema.*;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.types.EntityRefObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.PathLinkEndObjectTypeCreator;
import org.rutebanken.tiamat.rest.graphql.types.PathLinkObjectTypeCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.scalars.DateScalar.GraphQLDateScalar;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.*;

@Component
public class StopPlaceRegisterGraphQLSchema {

    private final int DEFAULT_PAGE_VALUE = 0;
    private final int DEFAULT_SIZE_VALUE = 20;

    public GraphQLSchema stopPlaceRegisterSchema;

    @Autowired
    private PathLinkEndObjectTypeCreator pathLinkEndObjectTypeCreator;

    @Autowired
    private PathLinkObjectTypeCreator pathLinkObjectTypeCreator;

    @Autowired
    private EntityRefObjectTypeCreator entityRefObjectTypeCreator;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    DataFetcher stopPlaceFetcher;

    @Autowired
    DataFetcher pathLinkFetcher;

    @Autowired
    DataFetcher pathLinkUpdater;

    @Autowired
    DataFetcher topographicPlaceFetcher;

    @Autowired
    DataFetcher stopPlaceUpdater;


    @PostConstruct
    public void init() {

        List<GraphQLFieldDefinition> commonFieldsList = new ArrayList<>();
        commonFieldsList.add(newFieldDefinition().name(NAME).type(embeddableMultilingualStringObjectType).build());
        commonFieldsList.add(newFieldDefinition().name(SHORT_NAME).type(embeddableMultilingualStringObjectType).build());
        commonFieldsList.add(newFieldDefinition().name(DESCRIPTION).type(embeddableMultilingualStringObjectType).build());
        commonFieldsList.add(newFieldDefinition()
                .name(PLACE_EQUIPMENTS)
                .type(equipmentType)
                .dataFetcher(env -> {
                    if (env.getSource() instanceof StopPlace) {
                        return ((StopPlace)env.getSource()).getPlaceEquipments();
                    } else if (env.getSource() instanceof Quay) {
                        return ((Quay)env.getSource()).getPlaceEquipments();
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

        GraphQLFieldDefinition geometryFieldDefinition = newFieldDefinition()
                .name(GEOMETRY)
                .type(geoJsonObjectType)
                .dataFetcher(env -> {
                            if (env.getSource() instanceof Zone_VersionStructure) {
                                Zone_VersionStructure source = (Zone_VersionStructure) env.getSource();
                                return source.getCentroid();
                            } else if(env.getSource() instanceof Link) {
                                Link link = (Link) env.getSource();
                                return link.getLineString();
                            }
                            return null;
                        }).build();

        commonFieldsList.add(geometryFieldDefinition);

        commonFieldsList.add(newFieldDefinition()
                .name(IMPORTED_ID)
                .type(new GraphQLList(GraphQLString))
                .dataFetcher(env -> {
                    DataManagedObjectStructure source = (DataManagedObjectStructure) env.getSource();
                    if (source != null) {
                        return source.getOriginalIds();
                    } else {
                        return null;
                    }
                })
                .build());


        commonFieldsList.add(netexIdFieldDefinition);

        GraphQLObjectType quayObjectType = createQuayObjectType(commonFieldsList);

        GraphQLObjectType validBetweenObjectType = createValidBetweenObjectType();

        GraphQLObjectType stopPlaceObjectType = createStopPlaceObjectType(commonFieldsList, quayObjectType, validBetweenObjectType);

        GraphQLObjectType addressablePlaceObjectType = createAddressablePlaceObjectType(commonFieldsList);

        GraphQLObjectType entityRefObjectType = entityRefObjectTypeCreator.create(addressablePlaceObjectType);

        GraphQLObjectType pathLinkEndObjectType = pathLinkEndObjectTypeCreator.create(entityRefObjectType, netexIdFieldDefinition);

        GraphQLObjectType pathLinkObjectType = pathLinkObjectTypeCreator.create(pathLinkEndObjectType, netexIdFieldDefinition, geometryFieldDefinition);

        GraphQLArgument allVersionsArgument = GraphQLArgument.newArgument()
                .name(ALL_VERSIONS)
                .type(GraphQLBoolean)
                .description("Fetch all versions for entitites in result")
                .build();

        GraphQLObjectType stopPlaceRegisterQuery = newObject()
                .name("StopPlaceRegister")
                .description("Query and search for data")
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(FIND_STOPPLACE)
                        .description("Search for StopPlaces")
                        .argument(createFindStopPlaceArguments(allVersionsArgument))
                        .dataFetcher(stopPlaceFetcher))
                        //Search by BoundingBox
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
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
                .build();


        List<GraphQLInputObjectField> commonInputFieldList = createCommonInputFieldList(embeddableMultiLingualStringInputObjectType);

        GraphQLInputObjectType quayInputObjectType = createQuayInputObjectType(commonInputFieldList);

        GraphQLInputObjectType validBetweenInputObjectType = createValidBetweenInputObjectType();

        GraphQLInputObjectType stopPlaceInputObjectType = createStopPlaceInputObjectType(commonInputFieldList,
                topographicPlaceInputObjectType, quayInputObjectType, validBetweenInputObjectType);

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
                        .type(new GraphQLList(pathLinkObjectType))
                        .name(MUTATE_PATH_LINK)
                        .description("Create new or update existing PathLink")
                        .argument(GraphQLArgument.newArgument()
                                .name(OUTPUT_TYPE_PATH_LINK)
                                .type(new GraphQLList(pathLinkObjectInputType)))
                        .description("Create new or update existing "+OUTPUT_TYPE_PATH_LINK)
                        .dataFetcher(pathLinkUpdater))
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
                .field(newFieldDefinition()
                        .name(VERSION)
                        .type(GraphQLString))
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
        List<GraphQLArgument> arguments = new ArrayList<>();
        arguments.addAll(createPageAndSizeArguments());
        arguments.add(allVersionsArgument);
                //Search
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .description("IDs used to lookup StopPlace(s). When used - all other searchparameters are ignored.")
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(STOP_PLACE_TYPE)
                .type(new GraphQLList(stopPlaceTypeEnum))
                .description("Only return StopPlaces with given StopPlaceType(s).")
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(COUNTY_REF)
                .type(new GraphQLList(GraphQLString))
                .description("Only return StopPlaces located in given counties.")
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(MUNICIPALITY_REF)
                .type(new GraphQLList(GraphQLString))
                .description("Only return StopPlaces located in given municipalities.")
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .description("Searches for StopPlace by name.")
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(IMPORTED_ID_QUERY)
                .type(GraphQLString)
                .description("Searches for StopPlace by importedId.")
                .build());
        return arguments;
    }

    private List<GraphQLArgument> createBboxArguments() {
        List<GraphQLArgument> arguments = new ArrayList<>();
        arguments.addAll(createPageAndSizeArguments());
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
        return arguments;
    }

    private List<GraphQLArgument> createPageAndSizeArguments() {
        List<GraphQLArgument> arguments = new ArrayList<>();
        arguments.add(GraphQLArgument.newArgument()
                .name(PAGE)
                .type(GraphQLInt)
                .defaultValue(DEFAULT_PAGE_VALUE)
                .description("Pagenumber when using pagination - default is " + DEFAULT_PAGE_VALUE)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(SIZE)
                .type(GraphQLInt)
                .defaultValue(DEFAULT_SIZE_VALUE)
                .description("Number of hits per page when using pagination - default is " + DEFAULT_SIZE_VALUE)
                .build());
        return arguments;
    }


    private GraphQLObjectType createStopPlaceObjectType(List<GraphQLFieldDefinition> commonFieldsList,
                                                        GraphQLObjectType quayObjectType, GraphQLObjectType validBetweenObjectType) {
        return newObject()
                    .name(OUTPUT_TYPE_STOPPLACE)
                    .fields(commonFieldsList)
                    .field(newFieldDefinition()
                            .name(STOP_PLACE_TYPE)
                            .type(stopPlaceTypeEnum))
                    .field(newFieldDefinition()
                            .name(ALL_AREAS_WHEELCHAIR_ACCESSIBLE)
                            .type(GraphQLBoolean))
                    .field(newFieldDefinition()
                            .name(TOPOGRAPHIC_PLACE)
                            .type(topographicPlaceObjectType))
                    .field(newFieldDefinition()
                            .name(VERSION)
                            .type(GraphQLString))
                    .field(newFieldDefinition()
                            .name(QUAYS)
                            .type(new GraphQLList(quayObjectType)))
                    .field(newFieldDefinition()
                            .name(VALID_BETWEENS)
                            .type(new GraphQLList(validBetweenObjectType)))
                    .build();
    }

    private GraphQLObjectType createQuayObjectType(List<GraphQLFieldDefinition> commonFieldsList) {
        return newObject()
                    .name(OUTPUT_TYPE_QUAY)
                    .fields(commonFieldsList)
                    .field(newFieldDefinition()
                            .name(COMPASS_BEARING)
                            .type(GraphQLBigDecimal))
                    .field(newFieldDefinition()
                            .name(ALL_AREAS_WHEELCHAIR_ACCESSIBLE)
                            .type(GraphQLBoolean))
                    .field(newFieldDefinition()
                            .name(VERSION)
                            .type(GraphQLString))
                    .field(newFieldDefinition()
                            .name(PUBLIC_CODE)
                            .type(GraphQLString))
                    .build();
    }

    private GraphQLObjectType createValidBetweenObjectType() {
        return newObject()
                .name(OUTPUT_TYPE_VALID_BETWEEN)
                .field(newFieldDefinition()
                        .name(VALID_BETWEEN_FROM_DATE)
                        .type(GraphQLDateScalar)
                        .description(DATE_SCALAR_DESCRIPTION))
                .field(newFieldDefinition()
                        .name(VALID_BETWEEN_TO_DATE)
                        .type(GraphQLDateScalar)
                        .description(DATE_SCALAR_DESCRIPTION))
                .build();
    }

    private GraphQLInputObjectType createValidBetweenInputObjectType() {
        return GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_VALID_BETWEEN)
                .field(newInputObjectField()
                        .name(VALID_BETWEEN_FROM_DATE)
                        .type(new GraphQLNonNull(GraphQLDateScalar))
                        .description("When the new version is valid from"))
                .field(newInputObjectField()
                        .name(VALID_BETWEEN_TO_DATE)
                        .type(GraphQLDateScalar)
                        .description("When the version is no longer valid"))
                .build();
    }


    private GraphQLInputObjectType createStopPlaceInputObjectType(List<GraphQLInputObjectField> commonInputFieldsList,
                                                                  GraphQLInputObjectType topographicPlaceInputObjectType,
                                                                  GraphQLInputObjectType quayObjectInputType,
                                                                  GraphQLInputObjectType validBetweenInputObjectType) {
        return GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_STOPPLACE)
                .fields(commonInputFieldsList)
                .field(newInputObjectField()
                        .name(STOP_PLACE_TYPE)
                        .type(stopPlaceTypeEnum))
                .field(newInputObjectField()
                        .name(ALL_AREAS_WHEELCHAIR_ACCESSIBLE)
                        .type(GraphQLBoolean))
                .field(newInputObjectField()
                        .name(TOPOGRAPHIC_PLACE)
                        .type(topographicPlaceInputObjectType))
                .field(newInputObjectField()
                        .name(QUAYS)
                        .type(new GraphQLList(quayObjectInputType)))
                .field(newInputObjectField()
                        .name(VALID_BETWEENS)
                        .type(new GraphQLList(validBetweenInputObjectType)))
                .build();
    }

    private List<GraphQLInputObjectField> createCommonInputFieldList(GraphQLInputObjectType embeddableMultiLingualStringInputObjectType) {

        List<GraphQLInputObjectField> commonInputFieldsList = new ArrayList<>();
        commonInputFieldsList.add(newInputObjectField().name(ID).type(GraphQLString).description("Ignore when creating new").build());
        commonInputFieldsList.add(newInputObjectField().name(NAME).type(embeddableMultiLingualStringInputObjectType).build());
        commonInputFieldsList.add(newInputObjectField().name(SHORT_NAME).type(embeddableMultiLingualStringInputObjectType).build());
        commonInputFieldsList.add(newInputObjectField().name(DESCRIPTION).type(embeddableMultiLingualStringInputObjectType).build());
        commonInputFieldsList.add(newInputObjectField().name(GEOMETRY).type(geoJsonInputType).build());
        commonInputFieldsList.add(newInputObjectField().name(PLACE_EQUIPMENTS).type(equipmentInputType).build());
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
        return GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_QUAY)
                .fields(graphQLCommonInputObjectFieldsList)
                .field(newInputObjectField()
                        .name(COMPASS_BEARING)
                        .type(GraphQLBigDecimal))
                .field(newInputObjectField()
                        .name(ALL_AREAS_WHEELCHAIR_ACCESSIBLE)
                        .type(GraphQLBoolean))
                .field(newInputObjectField()
                        .name(PUBLIC_CODE)
                        .type(GraphQLString))
                .build();
    }
}

