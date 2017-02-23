package org.rutebanken.tiamat.rest.graphql.types;

import com.vividsolutions.jts.geom.Geometry;
import graphql.schema.*;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.getNetexId;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.scalars.CustomScalars.GraphQLGeoJSONCoordinates;

public class CustomGraphQLTypes {

    public static GraphQLEnumType topographicPlaceTypeEnum = GraphQLEnumType.newEnum()
            .name(TOPOGRAPHIC_PLACE_TYPE_ENUM)
            .value(TopographicPlaceTypeEnumeration.COUNTY.value(), TopographicPlaceTypeEnumeration.COUNTY)
            .value(TopographicPlaceTypeEnumeration.TOWN.value(), TopographicPlaceTypeEnumeration.TOWN)
            .build();

    public static GraphQLEnumType stopPlaceTypeEnum = GraphQLEnumType.newEnum()
            .name(STOP_PLACE_TYPE_ENUM)
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

    public static GraphQLObjectType geoJsonObjectType = newObject()
            .name(OUTPUT_TYPE_GEO_JSON)
            .field(newFieldDefinition()
                    .name("type")
                    .type(GraphQLString)
                    .dataFetcher(env -> {
                        if (env.getSource() instanceof Geometry) {
                            return env.getSource().getClass().getSimpleName();
                        }
                        return null;
                    }))
            .field(newFieldDefinition()
                    .name("coordinates")
                    .type(GraphQLGeoJSONCoordinates))
            .build();

    public static GraphQLInputObjectType geoJsonInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_GEO_JSON)
            .field(newInputObjectField()
                    .name(TYPE)
                    .type(new GraphQLNonNull(GraphQLString))
                    .description("Type of geometry. Valid inputs are 'Point' or 'LineString'.")
                    .build())
            .field(newInputObjectField()
                    .name(COORDINATES)
                    .type(new GraphQLNonNull(GraphQLGeoJSONCoordinates))
                    .build())
            .build();

    public static GraphQLObjectType embeddableMultilingualStringObjectType = newObject()
            .name(OUTPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING)
            .field(newFieldDefinition()
                    .name(VALUE)
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name(LANG)
                    .type(GraphQLString))
            .build();

    public static GraphQLObjectType topographicParentPlaceObjectType = newObject()
            .name(OUTPUT_TYPE_TOPOGRAPHIC_PLACE)
            .field(newFieldDefinition()
                    .name(ID)
                    .type(GraphQLString)
                    .dataFetcher(env -> {
                        TopographicPlace topographicPlace = (TopographicPlace) env.getSource();
                        if (topographicPlace != null) {
                            return getNetexId(topographicPlace);
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

    public static GraphQLObjectType topographicPlaceObjectType = newObject()
            .name(OUTPUT_TYPE_TOPOGRAPHIC_PLACE)
            .field(newFieldDefinition()
                    .name(ID)
                    .type(GraphQLString)
                    .dataFetcher(env -> {
                        TopographicPlace tp = (TopographicPlace) env.getSource();
                        if (tp != null) {
                            return getNetexId(tp);
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

    public static GraphQLFieldDefinition netexIdFieldDefinition = newFieldDefinition()
            .name(ID)
            .type(GraphQLString)
            .dataFetcher(env -> {
                if (env.getSource() instanceof EntityStructure) {
                    return NetexIdMapper.getNetexId((EntityStructure) env.getSource());
                } else if (env.getSource() instanceof PathLinkEnd) {
                    return NetexIdMapper.getNetexId((PathLinkEnd) env.getSource());
                }
                return null;
            })
            .build();

    public static GraphQLInputObjectType embeddableMultiLingualStringInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING)
            .field(newInputObjectField()
                    .name(VALUE)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(LANG)
                    .type(GraphQLString))
            .build();

    public static GraphQLInputObjectType topographicPlaceInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_TOPOGRAPHIC_PLACE)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(TOPOGRAPHIC_PLACE_TYPE)
                    .type(topographicPlaceTypeEnum))
            .field(newInputObjectField()
                    .name(NAME)
                    .type(embeddableMultiLingualStringInputObjectType))
            .build();

    public static GraphQLInputObjectType transferDurationInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_TRANSFER_DURATION)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name("defaultDuration")
                    .type(GraphQLInt)
                    .description("Default duration in seconds"))
            .field(newInputObjectField()
                    .name("frequentTravellerDuration")
                    .type(GraphQLInt)
                    .description("Frequent traveller duration in seconds"))
            .field(newInputObjectField()
                    .name("occasionalTravellerDuration")
                    .type(GraphQLInt)
                    .description("Occasional traveller duration in seconds"))
            .build();
    
    public static GraphQLInputObjectType quayIdReferenceInputObjectType = GraphQLInputObjectType
            .newInputObject()
            .name("Quay_id")
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .build();

    public static GraphQLInputType pathLinkEndInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_PATH_LINK_END)
            .field(newInputObjectField()
                    .name("quay")
                    .type(quayIdReferenceInputObjectType))
            .build();

    public static GraphQLInputObjectType pathLinkObjectInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_PATH_LINK)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(PATH_LINK_FROM)
                    .type(pathLinkEndInputObjectType))
            .field(newInputObjectField()
                    .name(PATH_LINK_TO)
                    .type(pathLinkEndInputObjectType))
            .field(newInputObjectField()
                    .name("transferDuration")
                    .type(transferDurationInputObjectType))
            .field(newInputObjectField()
                    .name(GEOMETRY)
                    .type(geoJsonInputType))
            .description("Transfer durations in seconds")
            .build();
}
