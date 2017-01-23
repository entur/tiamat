package org.rutebanken.tiamat.rest.graphql;

import graphql.annotations.GraphQLAnnotations;
import graphql.schema.*;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
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
    DataFetcher stopPlaceFetcher;

    @Autowired
    DataFetcher topographicPlaceFetcher;

    @Autowired
    DataFetcher stopPlaceUpdater;

    @PostConstruct
    public void init() {

        GraphQLEnumType topographicPlaceTypeEnum = GraphQLEnumType.newEnum()
                .name(TOPOGRAPHIC_PLACE_TYPE)
                .value(TopographicPlaceTypeEnumeration.COUNTY.value(), TopographicPlaceTypeEnumeration.COUNTY)
                .value(TopographicPlaceTypeEnumeration.TOWN.value(), TopographicPlaceTypeEnumeration.TOWN)
                .build();

        GraphQLEnumType stopPlaceTypeEnum = GraphQLEnumType.newEnum()
                .name(STOPPLACE_TYPE)
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


        GraphQLObjectType topographicPlaceObjectType;
        GraphQLObjectType stopPlaceObjectType;
        try {
            stopPlaceObjectType = GraphQLAnnotations.object(StopPlace.class);
            topographicPlaceObjectType = GraphQLAnnotations.object(TopographicPlace.class);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to start.", e);
        }

        GraphQLObjectType stopPlaceRegisterQuery = newObject()
                .name("SearchAndQuery")
                .description("Query and search for data")
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(FIND_STOPPPLACE_BY_ID)
                        .description("Retrieve StopPlace by ID")
                        .argument(GraphQLArgument.newArgument()
                                .name(ID)
                                .type(new GraphQLNonNull(GraphQLID))
                                .description("ID used to lookup StopPlace"))
                        .dataFetcher(stopPlaceFetcher))
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(STOPPLACE_SEARCH)
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
                                //Search
                        .argument(GraphQLArgument.newArgument()
                                .name(STOPPLACE_TYPE)
                                .type(new GraphQLList(stopPlaceTypeEnum))
                                .description("Only returns StopPlaces with given StopPlaceType(s)"))
                        .argument(GraphQLArgument.newArgument()
                                .name(COUNTY_REF)
                                .type(new GraphQLList(GraphQLInt))
                                .description("Only returns StopPlaces located in given county/counties"))
                        .argument(GraphQLArgument.newArgument()
                                .name(MUNICIPALITY_REF)
                                .type(new GraphQLList(GraphQLInt))
                                .description("Only returns StopPlaces located in given municipality/municipalities"))
                        .argument(GraphQLArgument.newArgument()
                                .name(QUERY)
                                .type(GraphQLString)
                                .description("Searches for StopPlaces with matching name."))
                        .dataFetcher(stopPlaceFetcher))
                        //Search by BoundingBox
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(FIND_STOPPLACE_BY_BBOX)
                        .description("Finds StopPlaces within given BoundingBox")
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
                                .description("Bottom left longitude (xMin)")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name(LATITUDE_MIN)
                                .description("Bottom left latitude (yMin)")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name(LONGITUDE_MAX)
                                .description("Top right longitude (xMax)")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name(LATITUDE_MAX)
                                .description("Top right longitude (yMax)")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name(IGNORE_STOPPLACE_ID)
                                .type(GraphQLID)
                                .description("ID of StopPlace to excluded from result"))
                        .dataFetcher(stopPlaceFetcher))
                .field(newFieldDefinition()
                        .name(FIND_TOPOGRAPHIC_PLACE)
                        .type(new GraphQLList(topographicPlaceObjectType))
                        .description("Find topographic places")
                        .argument(GraphQLArgument.newArgument()
                                .name(ID)
                                .type(GraphQLID))
                        .argument(GraphQLArgument.newArgument()
                                .name(TOPOGRAPHIC_PLACE_TYPE)
                                .type(topographicPlaceTypeEnum)
                                .description("Limits results to specified placeType"))
                        .argument(GraphQLArgument.newArgument()
                                .name(QUERY)
                                .type(GraphQLString)
                                .description("Searches for matching name"))
                        .dataFetcher(topographicPlaceFetcher))
                .build();

        GraphQLObjectType stopPlaceRegisterMutation = newObject()
                .name("StopPlaceMutation")
                .description("Create and edit stopplaces")
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(CREATE_STOPPLACE)
                        .description("Create new StopPlace")
                        .argument(GraphQLArgument.newArgument()
                                .name(NAME)
                                .type(GraphQLString))
                        .argument(GraphQLArgument.newArgument()
                                .name(SHORT_NAME)
                                .type(GraphQLString))
                        .argument(GraphQLArgument.newArgument()
                                .name(DESCRIPTION)
                                .type(GraphQLString))
                        .argument(GraphQLArgument.newArgument()
                                .name(STOPPLACE_TYPE)
                                .type(stopPlaceTypeEnum))
                        .argument(GraphQLArgument.newArgument()
                                .name(LATITUDE)
                                .type(GraphQLBigDecimal))
                        .argument(GraphQLArgument.newArgument()
                                .name(LONGITUDE)
                                .type(GraphQLBigDecimal))
                        .argument(GraphQLArgument.newArgument()
                                .name(WHEELCHAIR_ACCESSIBLE)
                                .type(GraphQLBoolean))
                        .dataFetcher(stopPlaceUpdater))
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(UPDATE_STOPPLACE)
                        .description("Update single StopPlace")
                        .argument(GraphQLArgument.newArgument()
                                .name(ID)
                                .type(new GraphQLNonNull(GraphQLID)))
                        .argument(GraphQLArgument.newArgument()
                                .name(STOPPLACE_TYPE)
                                .type(stopPlaceTypeEnum))
                        .argument(GraphQLArgument.newArgument()
                                .name(NAME)
                                .type(GraphQLString))
                        .dataFetcher(stopPlaceUpdater))
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(CREATE_QUAY)
                        .description("Create Quay and add to existing StopPlace defined by 'stopPlaceId'")
                        .argument(GraphQLArgument.newArgument()
                                .name(STOPPLACE_ID)
                                .type(new GraphQLNonNull(GraphQLID)))
                        .argument(GraphQLArgument.newArgument()
                                .name(STOPPLACE_TYPE)
                                .type(stopPlaceTypeEnum))
                        .argument(GraphQLArgument.newArgument()
                                .name(LATITUDE)
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name(LONGITUDE)
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .dataFetcher(stopPlaceUpdater))
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name(UPDATE_QUAY)
                        .description("Updates single Quay")
                        .argument(GraphQLArgument.newArgument()
                                .name(ID)
                                .type(new GraphQLNonNull(GraphQLID)))
                        .argument(GraphQLArgument.newArgument()
                                .name(STOPPLACE_TYPE)
                                .type(stopPlaceTypeEnum))
                        .argument(GraphQLArgument.newArgument()
                                .name(LATITUDE)
                                .type(GraphQLBigDecimal))
                        .argument(GraphQLArgument.newArgument()
                                .name(LONGITUDE)
                                .type(GraphQLBigDecimal))
                        .dataFetcher(stopPlaceUpdater))
                .build();

            stopPlaceRegisterSchema = GraphQLSchema.newSchema()
                .query(stopPlaceRegisterQuery)
                .mutation(stopPlaceRegisterMutation)
                .build();

    }
}

