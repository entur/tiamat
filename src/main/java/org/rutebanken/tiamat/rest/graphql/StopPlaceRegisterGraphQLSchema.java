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
                .name("topographicPlaceType")
                .value(TopographicPlaceTypeEnumeration.COUNTY.value(), TopographicPlaceTypeEnumeration.COUNTY)
                .value(TopographicPlaceTypeEnumeration.TOWN.value(), TopographicPlaceTypeEnumeration.TOWN)
                .build();

        GraphQLEnumType stopPlaceTypeEnum = GraphQLEnumType.newEnum()
                .name("stopPlaceType")
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
                .name("StopPlaceQuery")
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name("stopPlaceLookup")
                        .description("Fetches StopPlace by ID")
                        .argument(GraphQLArgument.newArgument()
                                .name("id")
                                .type(new GraphQLNonNull(GraphQLID))
                                .description("ID used to lookup StopPlace"))
                        .dataFetcher(stopPlaceFetcher))
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name("stopPlaceSearch")
                        .description("Searches for StopPlaces")
                        .argument(GraphQLArgument.newArgument()
                                .name("page")
                                .type(GraphQLInt)
                                .defaultValue(DEFAULT_PAGE_VALUE)
                                .description(PAGE_DESCRIPTION_TEXT))
                        .argument(GraphQLArgument.newArgument()
                                .name("size")
                                .type(GraphQLInt)
                                .defaultValue(DEFAULT_SIZE_VALUE)
                                .description(SIZE_DESCRIPTION_TEXT))
                                //Search
                        .argument(GraphQLArgument.newArgument()
                                .name("stopPlaceType")
                                .type(new GraphQLList(stopPlaceTypeEnum))
                                .description("Only returns StopPlaces with given StopPlaceType(s)"))
                        .argument(GraphQLArgument.newArgument()
                                .name("countyReference")
                                .type(new GraphQLList(GraphQLInt))
                                .description("Only returns StopPlaces located in given county/counties"))
                        .argument(GraphQLArgument.newArgument()
                                .name("municipalityReference")
                                .type(new GraphQLList(GraphQLInt))
                                .description("Only returns StopPlaces located in given municipality/municipalities"))
                        .argument(GraphQLArgument.newArgument()
                                .name("query")
                                .type(GraphQLString)
                                .description("Searches for StopPlaces with matching name."))
                        .dataFetcher(stopPlaceFetcher))
                //Search by BoundingBox
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name("stopPlaceBBox")
                        .description("Finds StopPlaces within given BoundingBox")
                        .argument(GraphQLArgument.newArgument()
                                .name("page")
                                .type(GraphQLInt)
                                .defaultValue(DEFAULT_PAGE_VALUE)
                                .description(PAGE_DESCRIPTION_TEXT))
                        .argument(GraphQLArgument.newArgument()
                                .name("size")
                                .type(GraphQLInt)
                                .defaultValue(DEFAULT_SIZE_VALUE)
                                .description(SIZE_DESCRIPTION_TEXT))
                                //BoundingBox
                        .argument(GraphQLArgument.newArgument()
                                .name("xMin")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name("yMin")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name("xMax")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name("yMax")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name("ignoreStopPlaceId")
                                .type(GraphQLID)
                                .description("ID of StopPlace to excluded from result"))
                        .dataFetcher(stopPlaceFetcher))
                .field(newFieldDefinition()
                        .name("topographicPlace")
                        .type(new GraphQLList(topographicPlaceObjectType))
                        .description("Finds topographic places")
                        .argument(GraphQLArgument.newArgument()
                                .name("id")
                                .type(GraphQLID))
                        .argument(GraphQLArgument.newArgument()
                                .name("topographicPlaceType")
                                .type(topographicPlaceTypeEnum)
                                .description("Limits results to specified placeType"))
                        .argument(GraphQLArgument.newArgument()
                                .name("query")
                                .type(GraphQLString)
                                .description("Searches for matching name"))
                        .dataFetcher(topographicPlaceFetcher))
                .build();

        GraphQLObjectType stopPlaceRegisterMutation = newObject()
                .name("StopPlaceMutation")
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name("updateStopPlace")
                        .description("Update single StopPlace")
                        .argument(GraphQLArgument.newArgument()
                                .name("id")
                                .type(new GraphQLNonNull(GraphQLID)))
                        .argument(GraphQLArgument.newArgument()
                                .name("stopPlaceType")
                                .type(stopPlaceTypeEnum))
                        .argument(GraphQLArgument.newArgument()
                                .name("name")
                                .type(GraphQLString))
                        .dataFetcher(stopPlaceUpdater))
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name("createStopPlace")
                        .description("Create new StopPlace")
                        .argument(GraphQLArgument.newArgument()
                                .name("name")
                                .type(GraphQLString))
                        .argument(GraphQLArgument.newArgument()
                                .name("stopPlaceType")
                                .type(stopPlaceTypeEnum))
                        .argument(GraphQLArgument.newArgument()
                                .name("latitude")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name("longitude")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .dataFetcher(stopPlaceUpdater))
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name("addQuay")
                        .description("Add Quay to existing StopPlace defined by 'stopPlaceId'")
                        .argument(GraphQLArgument.newArgument()
                                .name("stopPlaceId")
                                .type(new GraphQLNonNull(GraphQLID)))
                        .argument(GraphQLArgument.newArgument()
                                .name("stopPlaceType")
                                .type(new GraphQLNonNull(stopPlaceTypeEnum)))
                        .argument(GraphQLArgument.newArgument()
                                .name("latitude")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .argument(GraphQLArgument.newArgument()
                                .name("longitude")
                                .type(new GraphQLNonNull(GraphQLBigDecimal)))
                        .dataFetcher(stopPlaceUpdater))
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name("updateQuay")
                        .description("Updates single Quay")
                        .argument(GraphQLArgument.newArgument()
                                .name("id")
                                .type(new GraphQLNonNull(GraphQLID)))
                        .argument(GraphQLArgument.newArgument()
                                .name("stopPlaceType")
                                .type(stopPlaceTypeEnum))
                        .argument(GraphQLArgument.newArgument()
                                .name("latitude")
                                .type(GraphQLBigDecimal))
                        .argument(GraphQLArgument.newArgument()
                                .name("longitude")
                                .type(GraphQLBigDecimal))
                        .dataFetcher(stopPlaceUpdater))
                .build();

            stopPlaceRegisterSchema = GraphQLSchema.newSchema()
                .query(stopPlaceRegisterQuery)
                .mutation(stopPlaceRegisterMutation)
                .build();

    }
}

