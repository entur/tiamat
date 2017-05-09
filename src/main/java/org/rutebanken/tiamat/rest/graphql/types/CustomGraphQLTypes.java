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
            .value(TopographicPlaceTypeEnumeration.STATE.value(), TopographicPlaceTypeEnumeration.STATE)
            .value(TopographicPlaceTypeEnumeration.PLACE_OF_INTEREST.value(), TopographicPlaceTypeEnumeration.PLACE_OF_INTEREST)
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

     public static GraphQLEnumType interchangeWeightingEnum = GraphQLEnumType.newEnum()
             .name(INTERCHANGE_WEIGHTING_TYPE_ENUM)
             .value("noInterchange", InterchangeWeightingEnumeration.NO_INTERCHANGE)
             .value("interchangeAllowed", InterchangeWeightingEnumeration.INTERCHANGE_ALLOWED)
             .value("preferredInterchange", InterchangeWeightingEnumeration.PREFERRED_INTERCHANGE)
             .value("recommendedInterchange", InterchangeWeightingEnumeration.RECOMMENDED_INTERCHANGE)
             .build();
        public static GraphQLEnumType parkingVehicleEnum = GraphQLEnumType.newEnum()
                .name(PARKING_VEHICLE_ENUM)
                .value("car", ParkingVehicleEnumeration.CAR)
                .value("bus", ParkingVehicleEnumeration.BUS)
                .value("pedalCycle", ParkingVehicleEnumeration.PEDAL_CYCLE)
                .value("motorcycle", ParkingVehicleEnumeration.MOTORCYCLE)
                .build();

        public static GraphQLEnumType parkingLayoutEnum = GraphQLEnumType.newEnum()
                .name(PARKING_LAYOUT_ENUM)
                .value("covered", ParkingLayoutEnumeration.COVERED)
                .value("openSpace", ParkingLayoutEnumeration.OPEN_SPACE)
                .value("multistorey", ParkingLayoutEnumeration.MULTISTOREY)
                .value("underground", ParkingLayoutEnumeration.UNDERGROUND)
                .value("roadside", ParkingLayoutEnumeration.ROADSIDE)
                .value("other", ParkingLayoutEnumeration.OTHER)
                .build();

        public static GraphQLEnumType parkingUserEnum = GraphQLEnumType.newEnum()
                .name(PARKING_USER_ENUM)
                .value("all", ParkingUserEnumeration.ALL)
                .value("registered", ParkingUserEnumeration.REGISTERED)
                .value("registeredDisabled", ParkingUserEnumeration.REGISTERED_DISABLED)
                .value("residentsWithPermits", ParkingUserEnumeration.RESIDENTS_WITH_PERMITS)
                .build();

        public static GraphQLEnumType parkingStayEnum = GraphQLEnumType.newEnum()
                .name(PARKING_STAY_TYPE_ENUM)
                .value("shortStay", ParkingStayEnumeration.SHORT_STAY)
                .value("longStay", ParkingStayEnumeration.LONG_TERM)
                .value("dropoff", ParkingStayEnumeration.DROPOFF)
                .value("unlimited", ParkingStayEnumeration.UNLIMITED)
                .build();

        public static GraphQLEnumType parkingReservationEnum = GraphQLEnumType.newEnum()
                .name(PARKING_RESERVATION_ENUM)
                .value("noReservations", ParkingReservationEnumeration.NO_RESERVATIONS)
                .value("registrationRequired", ParkingReservationEnumeration.REGISTRATION_REQUIRED)
                .value("reservationRequired", ParkingReservationEnumeration.RESERVATION_REQUIRED)
                .value("reservationAllowed", ParkingReservationEnumeration.RESERVATION_ALLOWED)
                .value("other", ParkingReservationEnumeration.OTHER)
                .build();

        public static GraphQLEnumType parkingTypeEnum = GraphQLEnumType.newEnum()
                .name(PARKING_TYPE_ENUM)
                .value("parkAndRide", ParkingTypeEnumeration.PARK_AND_RIDE)
                .value("liftShareParking", ParkingTypeEnumeration.LIFT_SHARE_PARKING)
                .value("urbanParking", ParkingTypeEnumeration.URBAN_PARKING)
                .value("airportParking", ParkingTypeEnumeration.AIRPORT_PARKING)
                .value("trainStationParking", ParkingTypeEnumeration.TRAIN_STATION_PARKING)
                .value("exhibitionCentreParking", ParkingTypeEnumeration.EXHIBITION_CENTRE_PARKING)
                .value("rentalCarParking", ParkingTypeEnumeration.RENTAL_CAR_PARKING)
                .value("shoppingCentreParking", ParkingTypeEnumeration.SHOPPING_CENTRE_PARKING)
                .value("motorwayParking", ParkingTypeEnumeration.MOTORWAY_PARKING)
                .value("roadside", ParkingTypeEnumeration.ROADSIDE)
                .value("parkingZone", ParkingTypeEnumeration.PARKING_ZONE)
                .value("undefined", ParkingTypeEnumeration.UNDEFINED)
                .value("cycleRental", ParkingTypeEnumeration.CYCLE_RENTAL)
                .value("other", ParkingTypeEnumeration.OTHER)
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

        public static  GraphQLEnumType cycleStorageTypeEnum = GraphQLEnumType.newEnum()
            .name(CYCLE_STORAGE_TYPE)
            .value("bars", CycleStorageEnumeration.BARS)
            .value("racks", CycleStorageEnumeration.RACKS)
            .value("railings", CycleStorageEnumeration.RAILINGS)
            .value("cycleScheme", CycleStorageEnumeration.CYCLE_SCHEME)
            .value("other", CycleStorageEnumeration.OTHER)
            .build();

        public static  GraphQLEnumType genderTypeEnum = GraphQLEnumType.newEnum()
            .name(GENDER)
            .value("both", GenderLimitationEnumeration.BOTH)
            .value("femaleOnly", GenderLimitationEnumeration.FEMALE_ONLY)
            .value("maleOnly", GenderLimitationEnumeration.MALE_ONLY)
            .value("sameSexOnly", GenderLimitationEnumeration.SAME_SEX_ONLY)
            .build();

        public static GraphQLEnumType nameTypeEnum = GraphQLEnumType.newEnum()
                .name(NAME_TYPE)
                .value("alias", NameTypeEnumeration.ALIAS)
                .value("copy", NameTypeEnumeration.COPY)
                .value("label", NameTypeEnumeration.LABEL)
                .value("translation", NameTypeEnumeration.TRANSLATION)
                .value("other", NameTypeEnumeration.OTHER)
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


        public static GraphQLFieldDefinition geometryFieldDefinition = newFieldDefinition()
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

    public static GraphQLObjectType embeddableMultilingualStringObjectType = newObject()
            .name(OUTPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING)
            .field(newFieldDefinition()
                    .name(VALUE)
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name(LANG)
                    .type(GraphQLString))
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


        public static GraphQLObjectType shelterEquipmentType = newObject()
            .name(OUTPUT_TYPE_SHELTER_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .field(newFieldDefinition()
                    .name(STEP_FREE)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(ENCLOSED)
                    .type(GraphQLBoolean))
            .build();

    public static GraphQLInputObjectType shelterEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_SHELTER_EQUIPMENT)
            .field(newInputObjectField()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .field(newInputObjectField()
                    .name(STEP_FREE)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(ENCLOSED)
                    .type(GraphQLBoolean))
            .build();

    public static GraphQLObjectType ticketingEquipmentType = newObject()
            .name(OUTPUT_TYPE_TICKETING_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(TICKET_OFFICE)
                    .type(GraphQLBoolean))
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
                    .name(TICKET_OFFICE)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(TICKET_MACHINES)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(NUMBER_OF_MACHINES)
                    .type(GraphQLBigInteger))
            .build();

    public static GraphQLObjectType cycleStorageEquipmentType = newObject()
            .name(OUTPUT_TYPE_CYCLE_STORAGE_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(NUMBER_OF_SPACES)
                    .type(GraphQLBigInteger))
            .field(newFieldDefinition()
                    .name(CYCLE_STORAGE_TYPE)
                    .type(cycleStorageTypeEnum))
            .build();


    public static GraphQLInputObjectType cycleStorageEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_CYCLE_STORAGE_EQUIPMENT)
            .field(newInputObjectField()
                    .name(NUMBER_OF_SPACES)
                    .type(GraphQLBigInteger))
            .field(newInputObjectField()
                    .name(CYCLE_STORAGE_TYPE)
                    .type(cycleStorageTypeEnum))
            .build();

    public static GraphQLObjectType waitingRoomEquipmentType = newObject()
            .name(OUTPUT_TYPE_WAITING_ROOM_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .field(newFieldDefinition()
                    .name(HEATED)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(STEP_FREE)
                    .type(GraphQLBoolean))
            .build();

    public static GraphQLInputObjectType waitingRoomEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_WAITING_ROOM_EQUIPMENT)
            .field(newInputObjectField()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .field(newInputObjectField()
                    .name(HEATED)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(STEP_FREE)
                    .type(GraphQLBoolean))
            .build();

    public static GraphQLObjectType sanitaryEquipmentType = newObject()
            .name(OUTPUT_TYPE_SANITARY_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(NUMBER_OF_TOILETS)
                    .type(GraphQLBigInteger))
            .field(newFieldDefinition()
                    .name(GENDER)
                    .type(genderTypeEnum))
            .build();


    public static GraphQLInputObjectType sanitaryEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_SANITARY_EQUIPMENT)
            .field(newInputObjectField()
                    .name(NUMBER_OF_TOILETS)
                    .type(GraphQLBigInteger))
            .field(newInputObjectField()
                    .name(GENDER)
                    .type(genderTypeEnum))
            .build();


    public static GraphQLObjectType equipmentType = newObject()
            .name(OUTPUT_TYPE_PLACE_EQUIPMENTS)
            .field(netexIdFieldDefinition)
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
                    .name(CYCLE_STORAGE_EQUIPMENT)
                    .type(cycleStorageEquipmentType)
                    .dataFetcher(env -> {
                            List<InstalledEquipment_VersionStructure> installedEquipment = ((PlaceEquipment) env.getSource()).getInstalledEquipment();
                            for (InstalledEquipment_VersionStructure ie : installedEquipment) {
                                    if (ie instanceof CycleStorageEquipment) {
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
                    .name(WAITING_ROOM_EQUIPMENT)
                    .type(waitingRoomEquipmentInputType))
            .field(newInputObjectField()
                    .name(SANITARY_EQUIPMENT)
                    .type(sanitaryEquipmentInputType))
            .field(newInputObjectField()
                    .name(TICKETING_EQUIPMENT)
                    .type(ticketingEquipmentInputType))
            .field(newInputObjectField()
                    .name(SHELTER_EQUIPMENT)
                    .type(shelterEquipmentInputType))
            .field(newInputObjectField()
                    .name(CYCLE_STORAGE_EQUIPMENT)
                    .type(cycleStorageEquipmentInputType))
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

    public static GraphQLObjectType alternativeNameObjectType = newObject()
                .name(OUTPUT_TYPE_ALTERNATIVE_NAME)
                .field(newFieldDefinition()
                        .name(NAME_TYPE)
                        .type(new GraphQLNonNull(nameTypeEnum)))
                .field(newFieldDefinition()
                        .name(NAME)
                        .type(embeddableMultilingualStringObjectType))
                .build();


    public static GraphQLInputObjectType alternativeNameInputObjectType = GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_ALTERNATIVE_NAME)
                .field(newInputObjectField()
                        .name(NAME_TYPE)
                        .type(nameTypeEnum))
                .field(newInputObjectField()
                        .name(NAME)
                        .type(new GraphQLNonNull(embeddableMultiLingualStringInputObjectType)))
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


        public static GraphQLObjectType parkingCapacityObjectType = newObject()
                .name(OUTPUT_TYPE_PARKING_CAPACITY)
                .field(newFieldDefinition()
                        .name(PARKING_VEHICLE_TYPE)
                        .type(parkingVehicleEnum))
                .field(newFieldDefinition()
                        .name(PARKING_STAY_TYPE)
                        .type(parkingStayEnum))
                .field(newFieldDefinition()
                        .name(NUMBER_OF_SPACES)
                        .type(GraphQLBigInteger))
                .build();


        public static GraphQLInputObjectType parkingCapacityInputObjectType = GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_PARKING_CAPACITY)
                .field(newInputObjectField()
                        .name(PARKING_VEHICLE_TYPE)
                        .type(parkingVehicleEnum))
                .field(newInputObjectField()
                        .name(PARKING_STAY_TYPE)
                        .type(parkingStayEnum))
                .field(newInputObjectField()
                        .name(NUMBER_OF_SPACES)
                        .type(GraphQLBigInteger))
                .build();

        public static GraphQLObjectType parkingPropertiesObjectType = newObject()
                .name(OUTPUT_TYPE_PARKING_PROPERTIES)
                .field(newFieldDefinition()
                        .name(PARKING_USER_TYPES)
                        .type(new GraphQLList(parkingUserEnum)))
                .field(newFieldDefinition()
                        .name(MAXIMUM_STAY)
                        .type(GraphQLBigInteger))
                .field(newFieldDefinition()
                        .name(SPACES)
                        .type(new GraphQLList(parkingCapacityObjectType)))
                .build();

        public static GraphQLInputObjectType parkingPropertiesInputObjectType = GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_PARKING_PROPERTIES)
                .field(newInputObjectField()
                        .name(PARKING_USER_TYPES)
                        .type(new GraphQLList(parkingUserEnum)))
                .field(newInputObjectField()
                        .name(MAXIMUM_STAY)
                        .type(GraphQLBigInteger))
                .field(newInputObjectField()
                        .name(SPACES)
                        .type(new GraphQLList(parkingCapacityInputObjectType)))
                .build();

        public static GraphQLObjectType parkingAreaObjectType = newObject()
                .name(OUTPUT_TYPE_PARKING_AREA)
                .field(newFieldDefinition()
                        .name(LABEL)
                        .type(embeddableMultilingualStringObjectType))
                .field(newFieldDefinition()
                        .name(TOTAL_CAPACITY)
                        .type(GraphQLBigInteger))
                .field(newFieldDefinition()
                        .name(PARKING_PROPERTIES)
                        .type(parkingPropertiesObjectType))
                .build();

        public static GraphQLInputObjectType parkingAreaInputObjectType = GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_PARKING_AREA)
                .field(newInputObjectField()
                        .name(LABEL)
                        .type(embeddableMultiLingualStringInputObjectType))
                .field(newInputObjectField()
                        .name(TOTAL_CAPACITY)
                        .type(GraphQLBigInteger))
                .field(newInputObjectField()
                        .name(PARKING_PROPERTIES)
                        .type(parkingPropertiesInputObjectType))
                .build();

        public static GraphQLObjectType parkingObjectType = newObject()
                .name(OUTPUT_TYPE_PARKING)
                .field(netexIdFieldDefinition)
                .field(newFieldDefinition()
                        .name(VERSION)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(NAME)
                        .type(embeddableMultilingualStringObjectType))
                .field(newFieldDefinition()
                        .name(PARENT_SITE_REF)
                        .type(GraphQLString)
                        .dataFetcher(env -> {
                                SiteRefStructure parentSiteRef = ((Parking) env.getSource()).getParentSiteRef();
                                if (parentSiteRef != null) {
                                        return parentSiteRef.getRef();
                                }
                                return null;
                        })
                )
                .field(newFieldDefinition()
                        .name(TOTAL_CAPACITY)
                        .type(GraphQLBigInteger))
                .field(newFieldDefinition()
                        .name(PARKING_TYPE)
                        .type(parkingTypeEnum))
                .field(newFieldDefinition()
                        .name(PARKING_VEHICLE_TYPES)
                        .type(new GraphQLList(parkingVehicleEnum)))
                .field(newFieldDefinition()
                        .name(PARKING_LAYOUT)
                        .type(parkingLayoutEnum))
                .field(newFieldDefinition()
                        .name(PRINCIPAL_CAPACITY)
                        .type(GraphQLBigInteger))
                .field(newFieldDefinition()
                        .name(OVERNIGHT_PARKING_PERMITTED)
                        .type(GraphQLBoolean))
                .field(newFieldDefinition()
                        .name(RECHARGING_AVAILABLE)
                        .type(GraphQLBoolean))
                .field(newFieldDefinition()
                        .name(SECURE)
                        .type(GraphQLBoolean))
                .field(newFieldDefinition()
                        .name(REAL_TIME_OCCUPANCY_AVAILABLE)
                        .type(GraphQLBoolean))
                .field(newFieldDefinition()
                        .name(PARKING_RESERVATION)
                        .type(parkingReservationEnum))
                .field(newFieldDefinition()
                        .name(BOOKING_URL)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(FREE_PARKING_OUT_OF_HOURS)
                        .type(GraphQLBoolean))
                .field(newFieldDefinition()
                        .name(PARKING_PROPERTIES)
                        .type(new GraphQLList(parkingPropertiesObjectType)))
                .field(newFieldDefinition()
                        .name(PARKING_AREAS)
                        .type(new GraphQLList(parkingAreaObjectType)))
                .field(geometryFieldDefinition)
                .build();

        public static GraphQLInputObjectType parkingObjectInputType = GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_PARKING)
                .field(newInputObjectField()
                        .name(ID)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(NAME)
                        .type(embeddableMultiLingualStringInputObjectType))
                .field(newInputObjectField()
                        .name(PARENT_SITE_REF)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(TOTAL_CAPACITY)
                        .type(GraphQLBigInteger))
                .field(newInputObjectField()
                        .name(PARKING_TYPE)
                        .type(parkingTypeEnum))
                .field(newInputObjectField()
                        .name(PARKING_VEHICLE_TYPES)
                        .type(new GraphQLList(parkingVehicleEnum)))
                .field(newInputObjectField()
                        .name(PARKING_LAYOUT)
                        .type(parkingLayoutEnum))
                .field(newInputObjectField()
                        .name(PRINCIPAL_CAPACITY)
                        .type(GraphQLBigInteger))
                .field(newInputObjectField()
                        .name(OVERNIGHT_PARKING_PERMITTED)
                        .type(GraphQLBoolean))
                .field(newInputObjectField()
                        .name(RECHARGING_AVAILABLE)
                        .type(GraphQLBoolean))
                .field(newInputObjectField()
                        .name(SECURE)
                        .type(GraphQLBoolean))
                .field(newInputObjectField()
                        .name(REAL_TIME_OCCUPANCY_AVAILABLE)
                        .type(GraphQLBoolean))
                .field(newInputObjectField()
                        .name(PARKING_RESERVATION)
                        .type(parkingReservationEnum))
                .field(newInputObjectField()
                        .name(BOOKING_URL)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(FREE_PARKING_OUT_OF_HOURS)
                        .type(GraphQLBoolean))
                .field(newInputObjectField()
                        .name(PARKING_PROPERTIES)
                        .type(new GraphQLList(parkingPropertiesInputObjectType)))
                .field(newInputObjectField()
                        .name(PARKING_AREAS)
                        .type(new GraphQLList(parkingAreaInputObjectType)))
                .field(newInputObjectField()
                        .name(GEOMETRY)
                        .type(geoJsonInputType))
                .description("Transfer durations in seconds")
                .build();


}
