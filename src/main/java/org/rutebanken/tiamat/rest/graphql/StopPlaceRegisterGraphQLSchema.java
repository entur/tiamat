package org.rutebanken.tiamat.rest.graphql;

import graphql.Scalars;
import graphql.annotations.GraphQLAnnotations;
import graphql.schema.*;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class StopPlaceRegisterGraphQLSchema {

    public GraphQLSchema stopPlaceRegisterSchema;

    @Autowired
    DataFetcher stopPlaceFetcher;

    @PostConstruct
    public void init() {

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

        GraphQLObjectType stopPlaceDtoObjectType;
        try {
            stopPlaceDtoObjectType = GraphQLAnnotations.object(StopPlace.class);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to start.", e);
        }

        GraphQLObjectType queryType = newObject()
                .name("findStopPlaces")
                .field(newFieldDefinition()
                                .type(new GraphQLList(stopPlaceDtoObjectType))
                                .name("stopPlace")
                                        //Common
                                .argument(GraphQLArgument.newArgument()
                                        .name("page")
                                        .type(Scalars.GraphQLInt)
                                        .defaultValue(0))
                                .argument(GraphQLArgument.newArgument()
                                        .name("size")
                                        .type(Scalars.GraphQLInt)
                                        .defaultValue(20))
                                        //Search
                                .argument(GraphQLArgument.newArgument()
                                        .name("id")
                                        .type(new GraphQLList(Scalars.GraphQLLong)))
                                .argument(GraphQLArgument.newArgument()
                                        .name("stopPlaceType")
                                        .type(new GraphQLList(stopPlaceTypeEnum)))
                                .argument(GraphQLArgument.newArgument()
                                        .name("countyReference")
                                        .type(new GraphQLList(Scalars.GraphQLInt)))
                                .argument(GraphQLArgument.newArgument()
                                        .name("municipalityReference")
                                        .type(new GraphQLList(Scalars.GraphQLInt)))
                                .argument(GraphQLArgument.newArgument()
                                        .name("query")
                                        .type(Scalars.GraphQLString))

                                        //BoundingBox
                                .argument(GraphQLArgument.newArgument()
                                        .name("xMin")
                                        .type(Scalars.GraphQLBigDecimal))
                                .argument(GraphQLArgument.newArgument()
                                        .name("yMin")
                                        .type(Scalars.GraphQLBigDecimal))
                                .argument(GraphQLArgument.newArgument()
                                        .name("xMax")
                                        .type(Scalars.GraphQLBigDecimal))
                                .argument(GraphQLArgument.newArgument()
                                        .name("yMax")
                                        .type(Scalars.GraphQLBigDecimal))
                                .argument(GraphQLArgument.newArgument()
                                        .name("ignoreStopPlaceId")
                                        .type(Scalars.GraphQLLong))
                                .dataFetcher(stopPlaceFetcher)
                ).build();

        stopPlaceRegisterSchema = GraphQLSchema.newSchema()
                .query(queryType)
                .build();

    }
}

