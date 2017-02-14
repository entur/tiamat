package org.rutebanken.tiamat.rest.graphql;

import com.vividsolutions.jts.geom.Point;
import graphql.Scalars;
import graphql.schema.*;
import org.rutebanken.tiamat.dtoassembling.dto.LocationDto;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
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
@Component
public class StopPlaceRegisterGraphQLSchema {

    private final int DEFAULT_PAGE_VALUE = 0;
    private final int DEFAULT_SIZE_VALUE = 20;
    private final String PAGE_DESCRIPTION_TEXT = "Pagenumber when using pagination - default is " + DEFAULT_PAGE_VALUE;
    private final String SIZE_DESCRIPTION_TEXT = "Number of hits per page when using pagination - default is " + DEFAULT_SIZE_VALUE;

    public GraphQLSchema stopPlaceRegisterSchema;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    DataFetcher stopPlaceFetcher;

    @Autowired
    DataFetcher topographicPlaceFetcher;

    @Autowired
    DataFetcher stopPlaceUpdater;

    private static GraphQLEnumType topographicPlaceTypeEnum = GraphQLEnumType.newEnum()
            .name(OBJECT_TYPE_TOPOGRAPHIC_PLACE_TYPE)
            .value(TopographicPlaceTypeEnumeration.COUNTY.value(), TopographicPlaceTypeEnumeration.COUNTY)
            .value(TopographicPlaceTypeEnumeration.TOWN.value(), TopographicPlaceTypeEnumeration.TOWN)
            .build();

    private static GraphQLEnumType stopPlaceTypeEnum = GraphQLEnumType.newEnum()
            .name(OBJECT_TYPE_STOP_PLACE_TYPE)
            .value("onstreetBus", StopTypeEnumeration.ONSTREET_BUS)
            .value("onstreetTram", StopTypeEnumeration.ONSTREET_TRAM)
            .value("airport", StopTypeEnumeration.AIRPORT)
            .value("railStation", StopTypeEnumeration.RAIL_STATION)
            .value("metroStation", StopTypeEnumeration.METRO_STATION)
            .value("busStation", StopTypeEnumeration.BUS_STATION)
            .value("coachStation", StopTypeEnumeration.COACH_STATION)
            .value("tramStation", StopTypeEnumeration.TRAM_STATION)
            .value("harbourPort", StopTypeEnumeration.HARBOUR_PORT)
            .value("ferryPort", StopTypeEnumeration.FERRY_PORT)
            .value("ferryStop", StopTypeEnumeration.FERRY_STOP)
            .value("liftStation", StopTypeEnumeration.LIFT_STATION)
            .value("vehicleRailInterchange", StopTypeEnumeration.VEHICLE_RAIL_INTERCHANGE)
            .value("other", StopTypeEnumeration.OTHER)
            .build();

    @PostConstruct
    public void init() {

        GraphQLObjectType embeddableMultilingualStringObjectType = newObject()
                .name(OUTPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING)
                .field(newFieldDefinition()
                        .name(VALUE)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(LANG)
                        .type(GraphQLString))
                .build();

        GraphQLObjectType topographicParentPlaceObjectType = newObject()
                .name(OUTPUT_TYPE_TOPOGRAPHIC_PLACE)
                .field(newFieldDefinition()
                        .name(ID)
                        .type(GraphQLString)
                        .dataFetcher(env -> {
                            TopographicPlace tp = (TopographicPlace) env.getSource();
                            if (tp != null) {
                                return NetexIdMapper.getNetexId(new TopographicPlace(), tp.getId());
                            } else {
                                return null;
                            }
                        }))
                .field(newFieldDefinition()
                        .name(TOPOGRAPHIC_PLACE_TYPE)
                        .type(topographicPlaceTypeEnum))
                .field(newFieldDefinition()
                        .name(NAME)
                        .type(embeddableMultilingualStringObjectType))
                .build();

        GraphQLObjectType topographicPlaceObjectType = newObject()
                .name(OUTPUT_TYPE_TOPOGRAPHIC_PLACE)
                .field(newFieldDefinition()
                        .name(ID)
                        .type(GraphQLString)
                        .dataFetcher(env -> {
                                TopographicPlace tp = (TopographicPlace) env.getSource();
                            if (tp != null) {
                                return NetexIdMapper.getNetexId(new TopographicPlace(), tp.getId());
                            } else {
                                return null;
                            }
                        }))
                .field(newFieldDefinition()
                        .name(TOPOGRAPHIC_PLACE_TYPE)
                        .type(topographicPlaceTypeEnum))
                .field(newFieldDefinition()
                        .name(NAME)
                        .type(embeddableMultilingualStringObjectType))
                .field(newFieldDefinition()
                        .name(PARENT_TOPOGRAPHIC_PLACE)
                        .type(topographicParentPlaceObjectType)
                )
                .build();

        GraphQLObjectType locationObjectType = newObject()
                .name(OUTPUT_TYPE_LOCATION)
                .field(newFieldDefinition()
                        .name(LONGITUDE)
                        .type(GraphQLBigDecimal))
                .field(newFieldDefinition()
                        .name(LATITUDE)
                        .type(GraphQLBigDecimal))
                .build();

        List<GraphQLFieldDefinition> commonFieldsList = new ArrayList<>();
        commonFieldsList.add(newFieldDefinition().name(NAME).type(embeddableMultilingualStringObjectType).build());
        commonFieldsList.add(newFieldDefinition().name(SHORT_NAME).type(embeddableMultilingualStringObjectType).build());
        commonFieldsList.add(newFieldDefinition().name(DESCRIPTION).type(embeddableMultilingualStringObjectType).build());
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
        commonFieldsList.add(newFieldDefinition()
                .name(ID)
                .type(GraphQLString)
                .dataFetcher(env -> NetexIdMapper.getNetexId((EntityStructure) env.getSource(), ((EntityStructure) env.getSource()).getId()))
                .build());
        commonFieldsList.add(newFieldDefinition()
                .name(LOCATION)
                .type(locationObjectType)
                .dataFetcher(env -> {
                    Zone_VersionStructure source = (Zone_VersionStructure) env.getSource();
                    Point point = source.getCentroid();
                    if (point != null) {
                        LocationDto dto = new LocationDto();
                        dto.longitude = point.getX();
                        dto.latitude = point.getY();
                        return dto;
                    } else {
                        return null;
                    }
                }).build());

        GraphQLType quayObjectType = newObject()
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
                .build();

        GraphQLObjectType stopPlaceObjectType  = newObject()
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
                .build();


        GraphQLObjectType stopPlaceRegisterMutation = newObject()
                .name("StopPlaceMutation")
                .description("Create and edit stopplaces")
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(MUTATE_STOPPLACE)
                        .description("Create new or update existing StopPlace")
                        .argument(GraphQLArgument.newArgument().name(OUTPUT_TYPE_STOPPLACE).type(createStopPlaceObjectInputType()))
                        .dataFetcher(stopPlaceUpdater))
                .build();

            stopPlaceRegisterSchema = GraphQLSchema.newSchema()
                .query(stopPlaceRegisterQuery)
                .mutation(stopPlaceRegisterMutation)
                .build();

    }

    private GraphQLInputType createStopPlaceObjectInputType() {

        GraphQLInputObjectType locationInputType = GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_LOCATION)
                .field(newInputObjectField()
                        .name("latitude")
                        .type(new GraphQLNonNull(Scalars.GraphQLFloat))
                        .build())
                .field(newInputObjectField()
                        .name("longitude")
                        .type(new GraphQLNonNull(Scalars.GraphQLFloat))
                        .build())
                .build();

        GraphQLInputObjectType embeddableInputType = GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING)
                .field(newInputObjectField()
                        .name(VALUE)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(LANG)
                        .type(GraphQLString))
                .build();

        GraphQLInputObjectType topographicPlaceInputObjectType = GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_TOPOGRAPHIC_PLACE)
                .field(newInputObjectField()
                        .name(ID)
                        .type(GraphQLLong))
                .field(newInputObjectField()
                        .name(TOPOGRAPHIC_PLACE_TYPE)
                        .type(topographicPlaceTypeEnum))
                .field(newInputObjectField()
                        .name(NAME)
                        .type(embeddableInputType))
                .build();

        List<GraphQLInputObjectField> commonInputFieldsList = new ArrayList<>();
        commonInputFieldsList.add(newInputObjectField().name(ID).type(GraphQLString).description("Ignore when creating new").build());
        commonInputFieldsList.add(newInputObjectField().name(NAME).type(embeddableInputType).build());
        commonInputFieldsList.add(newInputObjectField().name(SHORT_NAME).type(embeddableInputType).build());
        commonInputFieldsList.add(newInputObjectField().name(DESCRIPTION).type(embeddableInputType).build());
        commonInputFieldsList.add(newInputObjectField().name(LOCATION).type(locationInputType).build());

        GraphQLInputObjectType createQuayObjectInputType = GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_QUAY)
                .fields(commonInputFieldsList)
                .field(newInputObjectField()
                        .name(COMPASS_BEARING)
                        .type(GraphQLBigDecimal))
                .field(newInputObjectField()
                        .name(ALL_AREAS_WHEELCHAIR_ACCESSIBLE)
                        .type(GraphQLBoolean))
                .build();

        GraphQLInputObjectType stopPlaceObjectInputType  = GraphQLInputObjectType.newInputObject()
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
                        .type(new GraphQLList(createQuayObjectInputType)))
                .build();
        return stopPlaceObjectInputType;
    }
}

