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
                        .name("stopPlace")
                                //Common
                        .argument(GraphQLArgument.newArgument()
                                .name("page")
                                .type(GraphQLInt)
                                .defaultValue(0))
                        .argument(GraphQLArgument.newArgument()
                                .name("size")
                                .type(GraphQLInt)
                                .defaultValue(20))
                                //Search
                        .argument(GraphQLArgument.newArgument()
                                .name("id")
                                .type(GraphQLLong))
                        .argument(GraphQLArgument.newArgument()
                                .name("stopPlaceType")
                                .type(new GraphQLList(stopPlaceTypeEnum)))
                        .argument(GraphQLArgument.newArgument()
                                .name("countyReference")
                                .type(new GraphQLList(GraphQLInt)))
                        .argument(GraphQLArgument.newArgument()
                                .name("municipalityReference")
                                .type(new GraphQLList(GraphQLInt)))
                        .argument(GraphQLArgument.newArgument()
                                .name("query")
                                .type(GraphQLString))
                        .dataFetcher(stopPlaceFetcher))
                //Search by BoundinxBox
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name("stopPlaceBBox")
                                //Common
                        .argument(GraphQLArgument.newArgument()
                                .name("page")
                                .type(GraphQLInt)
                                .defaultValue(0))
                        .argument(GraphQLArgument.newArgument()
                                .name("size")
                                .type(GraphQLInt)
                                .defaultValue(20))
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
                                .type(GraphQLLong))
                        .dataFetcher(stopPlaceFetcher))
                .field(newFieldDefinition()
                                .name("topographicPlace")
                                .type(new GraphQLList(topographicPlaceObjectType))
                                .argument(GraphQLArgument.newArgument()
                                        .name("id")
                                        .type(GraphQLLong))
                                .argument(GraphQLArgument.newArgument()
                                        .name("topographicPlaceType")
                                        .type(topographicPlaceTypeEnum))
                                .argument(GraphQLArgument.newArgument()
                                        .name("query")
                                        .type(GraphQLString))
                                .dataFetcher(topographicPlaceFetcher))
                .build();

        GraphQLObjectType stopPlaceRegisterMutation = newObject()
                .name("StopPlaceMutation")
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name("updateStopPlace")
                        .argument(GraphQLArgument.newArgument()
                                .name("id")
                                .type(new GraphQLNonNull(GraphQLLong)))
                        .argument(GraphQLArgument.newArgument()
                                .name("stopPlaceType")
                                .type(stopPlaceTypeEnum))
                        .argument(GraphQLArgument.newArgument()
                                .name("name")
                                .type(GraphQLString))
                        .dataFetcher(stopPlaceUpdater))
                .field(newFieldDefinition()
                        .type(new GraphQLList(stopPlaceObjectType))
                        .name("addQuay")
                        .argument(GraphQLArgument.newArgument()
                                .name("stopPlaceId")
                                .type(new GraphQLNonNull(GraphQLLong)))
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
                        .argument(GraphQLArgument.newArgument()
                                .name("id")
                                .type(new GraphQLNonNull(GraphQLLong)))
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

