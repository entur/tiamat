package org.rutebanken.tiamat.rest.graphql.types;

import com.vividsolutions.jts.geom.Geometry;
import graphql.schema.*;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;

import java.util.List;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLObjectType.newObject;
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

    public static GraphQLEnumType geometryTypeEnum = GraphQLEnumType.newEnum()
            .name(GEOMETRY_TYPE_ENUM)
            .value("Point")
            .value("LineString")
            .value("Polygon")
            .value("MultiPoint")
            .value("MultiLineString")
            .value("MultiPolygon")
            .value("GeometryCollection")
            .build();

        public static  GraphQLEnumType limitationStatusEnum = GraphQLEnumType.newEnum()
            .name(LIMITATION_STATUS_ENUM)
            .value("FALSE", LimitationStatusEnumeration.FALSE)
            .value("TRUE", LimitationStatusEnumeration.TRUE)
            .value("PARTIAL", LimitationStatusEnumeration.PARTIAL)
            .value("UNKNOWN", LimitationStatusEnumeration.UNKNOWN)
            .build();

    public static GraphQLObjectType geoJsonObjectType = newObject()
            .name(OUTPUT_TYPE_GEO_JSON)
            .description("Geometry-object as specified in the GeoJSON-standard (http://geojson.org/geojson-spec.html).")
            .field(newFieldDefinition()
                    .name(TYPE)
                    .type(geometryTypeEnum)
                    .dataFetcher(env -> {
                            if (env.getSource() instanceof Geometry) {
                                    return env.getSource().getClass().getSimpleName();
                            }
                            return null;
                    }))
            .field(newFieldDefinition()
                    .name(COORDINATES)
                    .type(GraphQLGeoJSONCoordinates))
            .build();

    public static GraphQLInputObjectType geoJsonInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_GEO_JSON)
            .description("Geometry-object as specified in the GeoJSON-standard (http://geojson.org/geojson-spec.html).")
            .field(newInputObjectField()
                    .name(TYPE)
                    .type(new GraphQLNonNull(geometryTypeEnum))
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


    public static GraphQLObjectType shelterEquipmentType = newObject()
            .name(OUTPUT_TYPE_SHELTER_EQUIPMENT)
            .field(newFieldDefinition()
                    .name(ENCLOSED)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .build();

    public static GraphQLInputObjectType shelterEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_SHELTER_EQUIPMENT)
            .field(newInputObjectField()
                    .name(ENCLOSED)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .build();

    public static GraphQLObjectType ticketingEquipmentType = newObject()
            .name(OUTPUT_TYPE_TICKETING_EQUIPMENT)
            .field(newFieldDefinition()
                    .name(TICKET_MACHINES)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(NUMBER_OF_MACHINES)
                    .type(GraphQLBigInteger))
            .build();


    public static GraphQLInputObjectType ticketingEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_TICKETING_EQUIPMENT)
            .field(newInputObjectField()
                    .name(TICKET_MACHINES)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(NUMBER_OF_MACHINES)
                    .type(GraphQLBigInteger))
            .build();

    public static GraphQLObjectType waitingRoomEquipmentType = newObject()
            .name(OUTPUT_TYPE_WAITING_ROOM_EQUIPMENT)
            .field(newFieldDefinition()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .build();

    public static GraphQLInputObjectType waitingRoomEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_WAITING_ROOM_EQUIPMENT)
            .field(newInputObjectField()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .build();

    public static GraphQLObjectType sanitaryEquipmentType = newObject()
            .name(OUTPUT_TYPE_SANITARY_EQUIPMENT)
            .field(newFieldDefinition()
                    .name(NUMBER_OF_TOILETS)
                    .type(GraphQLBigInteger))
            .build();


    public static GraphQLInputObjectType sanitaryEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_SANITARY_EQUIPMENT)
            .field(newInputObjectField()
                    .name(NUMBER_OF_TOILETS)
                    .type(GraphQLBigInteger))
            .build();


    public static GraphQLObjectType equipmentType = newObject()
            .name(OUTPUT_TYPE_PLACE_EQUIPMENTS)
            .field(newFieldDefinition()
                    .name(SHELTER_EQUIPMENT)
                    .type(shelterEquipmentType)
                    .dataFetcher(env -> {
                            List<InstalledEquipment_VersionStructure> installedEquipment = ((PlaceEquipment) env.getSource()).getInstalledEquipment();
                            for (InstalledEquipment_VersionStructure ie : installedEquipment) {
                                    if (ie instanceof ShelterEquipment) {
                                            return ie;
                                    }
                            }
                            return null;
                    })
            )
            .field(newFieldDefinition()
                    .name(WAITING_ROOM_EQUIPMENT)
                    .type(waitingRoomEquipmentType)
                    .dataFetcher(env -> {
                            List<InstalledEquipment_VersionStructure> installedEquipment = ((PlaceEquipment) env.getSource()).getInstalledEquipment();
                            for (InstalledEquipment_VersionStructure ie : installedEquipment) {
                                    if (ie instanceof WaitingRoomEquipment) {
                                            return ie;
                                    }
                            }
                            return null;
                    })
            )
            .field(newFieldDefinition()
                    .name(SANITARY_EQUIPMENT)
                    .type(sanitaryEquipmentType)
                    .dataFetcher(env -> {
                            List<InstalledEquipment_VersionStructure> installedEquipment = ((PlaceEquipment) env.getSource()).getInstalledEquipment();
                            for (InstalledEquipment_VersionStructure ie : installedEquipment) {
                                    if (ie instanceof SanitaryEquipment) {
                                            return ie;
                                    }
                            }
                            return null;
                    })
            )
            .field(newFieldDefinition()
                    .name(TICKETING_EQUIPMENT)
                    .type(ticketingEquipmentType)
                    .dataFetcher(env -> {
                            List<InstalledEquipment_VersionStructure> installedEquipment = ((PlaceEquipment) env.getSource()).getInstalledEquipment();
                            for (InstalledEquipment_VersionStructure ie : installedEquipment) {
                                    if (ie instanceof TicketingEquipment) {
                                            return ie;
                                    }
                            }
                            return null;
                    })
            )
            .build();

    public static GraphQLInputObjectType equipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_PLACE_EQUIPMENTS)
            .field(newInputObjectField()
                    .name(SHELTER_EQUIPMENT)
                    .type(shelterEquipmentInputType))
            .field(newInputObjectField()
                    .name(WAITING_ROOM_EQUIPMENT)
                    .type(waitingRoomEquipmentInputType))
            .field(newInputObjectField()
                    .name(SANITARY_EQUIPMENT)
                    .type(sanitaryEquipmentInputType))
            .field(newInputObjectField()
                    .name(TICKETING_EQUIPMENT)
                    .type(ticketingEquipmentInputType))
            .build();

    public static GraphQLObjectType topographicParentPlaceObjectType = newObject()
            .name(OUTPUT_TYPE_TOPOGRAPHIC_PLACE)
            .field(newFieldDefinition()
                    .name(ID)
                    .type(GraphQLString)
                    .dataFetcher(env -> {
                        TopographicPlace topographicPlace = (TopographicPlace) env.getSource();
                        if (topographicPlace != null) {
                            return topographicPlace.getNetexId();
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
                        TopographicPlace topographicPlace = (TopographicPlace) env.getSource();
                        if (topographicPlace != null) {
                            return topographicPlace.getNetexId();
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

        public static GraphQLObjectType accessibilityLimitationsObjectType = newObject()
                .name(OUTPUT_TYPE_ACCESSIBILITY_LIMITATIONS)
                .field(newFieldDefinition()
                                .name(ID)
                                .type(GraphQLString)
                                .dataFetcher(env -> ((AccessibilityLimitation) env.getSource()).getNetexId())
                )
                .field(newFieldDefinition()
                        .name(VERSION)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(WHEELCHAIR_ACCESS)
                        .type(limitationStatusEnum))
                .field(newFieldDefinition()
                        .name(STEP_FREE_ACCESS)
                        .type(limitationStatusEnum))
                .field(newFieldDefinition()
                        .name(ESCALATOR_FREE_ACCESS)
                        .type(limitationStatusEnum))
                .field(newFieldDefinition()
                        .name(LIFT_FREE_ACCESS)
                        .type(limitationStatusEnum))
                .field(newFieldDefinition()
                        .name(AUDIBLE_SIGNALS_AVAILABLE)
                        .type(limitationStatusEnum))
                .build();

        public static GraphQLObjectType accessibilityAssessmentObjectType = newObject()
                .name(OUTPUT_TYPE_ACCESSIBILITY_ASSESSMENT)
                .field(newFieldDefinition()
                        .name(ID)
                        .type(GraphQLString)
                        .dataFetcher(env -> ((AccessibilityAssessment) env.getSource()).getNetexId())
                )
                .field(newFieldDefinition()
                        .name(VERSION)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(LIMITATIONS)
                        .type(accessibilityLimitationsObjectType)
                        .dataFetcher(env -> {
                                List<AccessibilityLimitation> limitations = ((AccessibilityAssessment) env.getSource()).getLimitations();
                                if (limitations != null && !limitations.isEmpty()) {
                                        return limitations.get(0);
                                }
                                return null;
                        }))
                .field(newFieldDefinition()
                        .name(MOBILITY_IMPAIRED_ACCESS)
                        .type(limitationStatusEnum))
                .build();

    public static GraphQLFieldDefinition netexIdFieldDefinition = newFieldDefinition()
            .name(ID)
            .type(GraphQLString)
            .dataFetcher(env -> {
                if (env.getSource() instanceof IdentifiedEntity) {
                    return ((IdentifiedEntity) env.getSource()).getNetexId();
                }
                return null;
            })
            .build();


        public static GraphQLInputObjectType accessibilityLimitationsInputObjectType = GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_ACCESSIBILITY_LIMITATIONS)
                .field(newInputObjectField()
                        .name(ID)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(WHEELCHAIR_ACCESS)
                        .type(new GraphQLNonNull(limitationStatusEnum)))
                .field(newInputObjectField()
                        .name(STEP_FREE_ACCESS)
                        .type(new GraphQLNonNull(limitationStatusEnum)))
                .field(newInputObjectField()
                        .name(ESCALATOR_FREE_ACCESS)
                        .type(new GraphQLNonNull(limitationStatusEnum)))
                .field(newInputObjectField()
                        .name(LIFT_FREE_ACCESS)
                        .type(new GraphQLNonNull(limitationStatusEnum)))
                .field(newInputObjectField()
                        .name(AUDIBLE_SIGNALS_AVAILABLE)
                        .type(new GraphQLNonNull(limitationStatusEnum)))
                .build();

        public static GraphQLInputObjectType accessibilityAssessmentInputObjectType = GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_ACCESSIBILITY_ASSESSMENT)
                .field(newInputObjectField()
                        .name(ID)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(LIMITATIONS)
                        .type(accessibilityLimitationsInputObjectType))
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
            .description(TRANSFER_DURATION_DESCRIPTION)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(DEFAULT_DURATION)
                    .type(GraphQLInt)
                    .description(DEFAULT_DURATION_DESCRIPTION))
            .field(newInputObjectField()
                    .name(FREQUENT_TRAVELLER_DURATION)
                    .type(GraphQLInt)
                    .description(FREQUENT_TRAVELLER_DURATION_DESCRIPTION))
            .field(newInputObjectField()
                    .name(OCCASIONAL_TRAVELLER_DURATION)
                    .type(GraphQLInt)
                    .description(OCCASIONAL_TRAVELLER_DURATION_DESCRIPTION))
            .field(newInputObjectField()
                    .name(MOBILITY_RESTRICTED_TRAVELLER_DURATION)
                    .type(GraphQLInt)
                    .description(MOBILITY_RESTRICTED_TRAVELLER_DURATION_DESCRIPTION))
            .build();
    
    public static GraphQLInputObjectType quayIdReferenceInputObjectType = GraphQLInputObjectType
            .newInputObject()
            .name("Quay_id")
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .build();

    public static GraphQLInputObjectType refInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_ENTITY_REF)
            .description(ENTITY_REF_DESCRIPTION)
            .field(newInputObjectField()
                    .name(ENTITY_REF_REF)
                    .type(GraphQLString)
                    .description(ENTITY_REF_REF_DESCRIPTION))
            .field(newInputObjectField()
                    .name(ENTITY_REF_VERSION)
                    .type(GraphQLString)
                    .description(ENTITY_REF_VERSION_DESCRIPTION))
            .build();

    public static GraphQLInputType pathLinkEndInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_PATH_LINK_END)
            .field(newInputObjectField()
                    .name(PATH_LINK_END_PLACE_REF)
                    .type(refInputObjectType))
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
                    .name(TRANSFER_DURATION)
                    .type(transferDurationInputObjectType))
            .field(newInputObjectField()
                    .name(GEOMETRY)
                    .type(geoJsonInputType))
            .description("Transfer durations in seconds")
            .build();
}
