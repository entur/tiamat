package org.rutebanken.tiamat.rest.graphql;

import graphql.schema.*;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
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
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.*;

@Component
public class StopPlaceRegisterGraphQLSchema {

    private final int DEFAULT_PAGE_VALUE = 0;
    private final int DEFAULT_SIZE_VALUE = 20;
    private final String PAGE_DESCRIPTION_TEXT = "Pagenumber when using pagination - default is " + DEFAULT_PAGE_VALUE;
    private final String SIZE_DESCRIPTION_TEXT = "Number of hits per page when using pagination - default is " + DEFAULT_SIZE_VALUE;

    public GraphQLSchema stopPlaceRegisterSchema;

    @Autowired
    private PathLinkEndObjectTypeCreator pathLinkEndObjectTypeCreator;

    @Autowired
    private PathLinkObjectTypeCreator pathLinkObjectTypeCreator;

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
                .name(GEOMETRY)
                .type(geoJsonObjectType)
                .dataFetcher(env -> {
                    if (env.getSource() instanceof Zone_VersionStructure) {
                        Zone_VersionStructure source = (Zone_VersionStructure) env.getSource();
                        return source.getCentroid();
                    }
                    return null;
                })
                .build());

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

        GraphQLObjectType stopPlaceObjectType = createStopPlaceObjectType(commonFieldsList, quayObjectType);

        GraphQLObjectType pathLinkEndObjectType = pathLinkEndObjectTypeCreator.create(quayObjectType, stopPlaceObjectType, netexIdFieldDefinition);

        GraphQLObjectType pathLinkObjectType = pathLinkObjectTypeCreator.create(pathLinkEndObjectType, netexIdFieldDefinition);

        GraphQLObjectType stopPlaceRegisterQuery = newObject()
                .name("StopPlaceRegister")
                .description("Query and search for data")
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(FIND_STOPPLACE)
                        .description("Search for StopPlaces")
                        .argument(GraphQLArgument.newArgument()
                                .name(PAGE)
                                .type(GraphQLInt)
                                .defaultValue(DEFAULT_PAGE_VALUE)
                                .description(PAGE_DESCRIPTION_TEXT))
                        .argument(GraphQLArgument.newArgument()
                                .name(SIZE)
                                .type(GraphQLInt)
                                .defaultValue(DEFAULT_SIZE_VALUE)
                                .description(SIZE_DESCRIPTION_TEXT))
                        .argument(GraphQLArgument.newArgument()
                                .name(ID)
                                .type(GraphQLString)
                                .description("IDs used to lookup StopPlace(s). When used - all other searchparameters are ignored."))
                                //Search
                        .argument(GraphQLArgument.newArgument()
                                .name(STOP_PLACE_TYPE)
                                .type(new GraphQLList(stopPlaceTypeEnum))
                                .description("Only return StopPlaces with given StopPlaceType(s)."))
                        .argument(GraphQLArgument.newArgument()
                                .name(COUNTY_REF)
                                .type(new GraphQLList(GraphQLString))
                                .description("Only return StopPlaces located in given counties."))
                        .argument(GraphQLArgument.newArgument()
                                .name(MUNICIPALITY_REF)
                                .type(new GraphQLList(GraphQLString))
                                .description("Only return StopPlaces located in given municipalities."))
                        .argument(GraphQLArgument.newArgument()
                                .name(QUERY)
                                .type(GraphQLString)
                                .description("Searches for StopPlace by name."))
                        .argument(GraphQLArgument.newArgument()
                                .name(IMPORTED_ID_QUERY)
                                .type(GraphQLString)
                                .description("Searches for StopPlace by importedId."))
                        .dataFetcher(stopPlaceFetcher))
                        //Search by BoundingBox
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(FIND_STOPPLACE_BY_BBOX)
                        .description("Find StopPlaces within given BoundingBox.")
                        .argument(GraphQLArgument.newArgument()
                                .name(PAGE)
                                .type(GraphQLInt)
                                .defaultValue(DEFAULT_PAGE_VALUE)
                                .description(PAGE_DESCRIPTION_TEXT))
                        .argument(GraphQLArgument.newArgument()
                                .name(SIZE)
                                .type(GraphQLInt)
                                .defaultValue(DEFAULT_SIZE_VALUE)
                                .description(SIZE_DESCRIPTION_TEXT))
                                //BoundingBox
                        .argument(GraphQLArgument.newArgument()
                                .name(LONGITUDE_MIN)
                                .description("Bottom left longitude (xMin).")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name(LATITUDE_MIN)
                                .description("Bottom left latitude (yMin).")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name(LONGITUDE_MAX)
                                .description("Top right longitude (xMax).")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name(LATITUDE_MAX)
                                .description("Top right longitude (yMax).")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name(IGNORE_STOPPLACE_ID)
                                .type(GraphQLString)
                                .description("ID of StopPlace to excluded from result."))
                        .dataFetcher(stopPlaceFetcher))
                .field(newFieldDefinition()
                        .name(FIND_TOPOGRAPHIC_PLACE)
                        .type(new GraphQLList(topographicPlaceObjectType))
                        .description("Find topographic places")
                        .argument(GraphQLArgument.newArgument()
                                .name(ID)
                                .type(GraphQLString))
                        .argument(GraphQLArgument.newArgument()
                                .name(TOPOGRAPHIC_PLACE_TYPE)
                                .type(topographicPlaceTypeEnum)
                                .description("Limits results to specified placeType."))
                        .argument(GraphQLArgument.newArgument()
                                .name(QUERY)
                                .type(GraphQLString)
                                .description("Searches for TopographicPlaces by name."))
                        .dataFetcher(topographicPlaceFetcher))
                .field(newFieldDefinition()
                        .name(FIND_PATH_LINK)
                        .type(new GraphQLList(pathLinkObjectType))
                        .description("Find path links")
                        .argument(GraphQLArgument.newArgument()
                                .name(ID)
                                .type(GraphQLString))
                        .dataFetcher(pathLinkFetcher))
                .build();


        List<GraphQLInputObjectField> commonInputFieldList = createCommonInputFieldList(embeddableMultiLingualStringInputObjectType);

        GraphQLInputObjectType quayInputObjectType = createQuayInputObjectType(commonInputFieldList);

        GraphQLInputObjectType stopPlaceInputObjectType = createStopPlaceInputObjectType(commonInputFieldList, topographicPlaceInputObjectType, quayInputObjectType);

        GraphQLObjectType stopPlaceRegisterMutation = newObject()
                .name("StopPlaceMutation")
                .description("Create and edit stopplaces")
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(MUTATE_STOPPLACE)
                        .description("Create new or update existing StopPlace")
                        .argument(GraphQLArgument.newArgument().name(OUTPUT_TYPE_STOPPLACE).type(stopPlaceInputObjectType))
                        .dataFetcher(stopPlaceUpdater))
                .field(newFieldDefinition()
                        .type(new GraphQLList(pathLinkObjectType))
                        .name(MUTATE_PATH_LINK)
                        .description("Create new or update existing "+OUTPUT_TYPE_PATH_LINK)
                        .argument(GraphQLArgument.newArgument().name(OUTPUT_TYPE_PATH_LINK).type(pathLinkObjectInputType))
                        .dataFetcher(pathLinkUpdater))
                .build();

        stopPlaceRegisterSchema = GraphQLSchema.newSchema()
                .query(stopPlaceRegisterQuery)
                .mutation(stopPlaceRegisterMutation)
                .build();
    }

    private GraphQLObjectType createStopPlaceObjectType(List<GraphQLFieldDefinition> commonFieldsList, GraphQLObjectType quayObjectType) {
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

    private GraphQLInputObjectType createStopPlaceInputObjectType(List<GraphQLInputObjectField> commonInputFieldsList,
                                                                  GraphQLInputObjectType topographicPlaceInputObjectType,
                                                                  GraphQLInputObjectType quayObjectInputType) {
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
                .build();
    }

    private List<GraphQLInputObjectField> createCommonInputFieldList(GraphQLInputObjectType embeddableMultiLingualStringInputObjectType) {

        List<GraphQLInputObjectField> commonInputFieldsList = new ArrayList<>();
        commonInputFieldsList.add(newInputObjectField().name(ID).type(GraphQLString).description("Ignore when creating new").build());
        commonInputFieldsList.add(newInputObjectField().name(NAME).type(embeddableMultiLingualStringInputObjectType).build());
        commonInputFieldsList.add(newInputObjectField().name(SHORT_NAME).type(embeddableMultiLingualStringInputObjectType).build());
        commonInputFieldsList.add(newInputObjectField().name(DESCRIPTION).type(embeddableMultiLingualStringInputObjectType).build());
        commonInputFieldsList.add(newInputObjectField().name(GEOMETRY).type(geoJsonInputType).build());

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

