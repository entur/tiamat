package org.rutebanken.tiamat.rest.graphql;

import static org.rutebanken.tiamat.rest.graphql.scalars.DateScalar.DATE_TIME_PATTERN;
import static org.rutebanken.tiamat.rest.graphql.scalars.DateScalar.EXAMPLE_DATE_TIME;

public class GraphQLNames {

    private static final String INPUT_TYPE_POSTFIX = "Input";

    public static final String OUTPUT_TYPE_TOPOGRAPHIC_PLACE = "TopographicPlace";
    public static final String INPUT_TYPE_TOPOGRAPHIC_PLACE = OUTPUT_TYPE_TOPOGRAPHIC_PLACE + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_ACCESSIBILITY_LIMITATIONS = "AccessibilityLimitations";
    public static final String INPUT_TYPE_ACCESSIBILITY_LIMITATIONS = OUTPUT_TYPE_ACCESSIBILITY_LIMITATIONS + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_ACCESSIBILITY_ASSESSMENT = "AccessibilityAssessment";
    public static final String INPUT_TYPE_ACCESSIBILITY_ASSESSMENT = OUTPUT_TYPE_ACCESSIBILITY_ASSESSMENT + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_PATH_LINK = "PathLink";
    public static final String INPUT_TYPE_PATH_LINK = OUTPUT_TYPE_PATH_LINK + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_PATH_LINK_END = "PathLinkEnd";
    public static final String INPUT_TYPE_PATH_LINK_END = OUTPUT_TYPE_PATH_LINK_END + INPUT_TYPE_POSTFIX;

    public static final String PATH_LINK_FROM = "from";
    public static final String PATH_LINK_TO = "to";

    public static final String PATH_LINK_END_PLACE_REF = "placeRef";

    public static final String OUTPUT_TYPE_ENTITY_REF = "EntityRef";
    public static final String INPUT_TYPE_ENTITY_REF = OUTPUT_TYPE_ENTITY_REF + INPUT_TYPE_POSTFIX;
    public static final String ENTITY_REF_DESCRIPTION = "A reference to an entity with version";

    public static final String ENTITY_REF_REF = "ref";
    public static final String ENTITY_REF_REF_DESCRIPTION = "The NeTEx ID of the of the referenced entity. The reference must already exist";
    public static final String ENTITY_REF_VERSION = "version";
    public static final String ENTITY_REF_VERSION_DESCRIPTION = "The version of the referenced entity. Specify \"any\" to always reference the newest version";
    public static final String ANY_VERSION = org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

    public static final String OUTPUT_TYPE_TARIFF_ZONE = "TariffZone";
    public static final String INPUT_TYPE_TARIFF_ZONE = "TariffZone" + INPUT_TYPE_POSTFIX;
    public static final String TARIFF_ZONES = "tariffZones";

    public static final String OUTPUT_TYPE_ADDRESSABLE_PLACE = "AddressablePlace";

    public static final String OUTPUT_TYPE_VALID_BETWEEN = "ValidBetween";
    public static final String INPUT_TYPE_VALID_BETWEEN = OUTPUT_TYPE_VALID_BETWEEN + INPUT_TYPE_POSTFIX;

    public static final String VALID_BETWEEN_FROM_DATE = "fromDate";
    public static final String VALID_BETWEEN_TO_DATE = "toDate";

    public static final String DATE_SCALAR_DESCRIPTION = "Date time using the format: " + DATE_TIME_PATTERN + ". Example: "+EXAMPLE_DATE_TIME;

    public static final String OUTPUT_TYPE_TRANSFER_DURATION = "TransferDuration";
    public static final String INPUT_TYPE_TRANSFER_DURATION = OUTPUT_TYPE_TRANSFER_DURATION + INPUT_TYPE_POSTFIX;

    public static final String TRANSFER_DURATION = "transferDuration";
    public static final String TRANSFER_DURATION_DESCRIPTION = "Transfer durations in seconds";

    public static final String DEFAULT_DURATION = "defaultDuration";
    public static final String DEFAULT_DURATION_DESCRIPTION = "Default duration in seconds";

    public static final String FREQUENT_TRAVELLER_DURATION = "frequentTravellerDuration";
    public static final String FREQUENT_TRAVELLER_DURATION_DESCRIPTION = "Frequent traveller duration in seconds";

    public static final String OCCASIONAL_TRAVELLER_DURATION = "occasionalTravellerDuration";
    public static final String OCCASIONAL_TRAVELLER_DURATION_DESCRIPTION = "Occasional traveller duration in seconds";

    public static final String MOBILITY_RESTRICTED_TRAVELLER_DURATION = "mobilityRestrictedTravellerDuration";
    public static final String MOBILITY_RESTRICTED_TRAVELLER_DURATION_DESCRIPTION = "Mobility restriced traveller duration in seconds";

    public static final String OUTPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING = "EmbeddableMultilingualString";
    public static final String INPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING = OUTPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_ALTERNATIVE_NAME = "AlternativeName";
    public static final String INPUT_TYPE_ALTERNATIVE_NAME = OUTPUT_TYPE_ALTERNATIVE_NAME + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_PLACE_EQUIPMENTS = "PlaceEquipments";
    public static final String INPUT_TYPE_PLACE_EQUIPMENTS = OUTPUT_TYPE_PLACE_EQUIPMENTS + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_SHELTER_EQUIPMENT = "ShelterEquipment";
    public static final String INPUT_TYPE_SHELTER_EQUIPMENT = OUTPUT_TYPE_SHELTER_EQUIPMENT + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_CYCLE_STORAGE_EQUIPMENT = "CycleStorageEquipment";
    public static final String INPUT_TYPE_CYCLE_STORAGE_EQUIPMENT = OUTPUT_TYPE_CYCLE_STORAGE_EQUIPMENT + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_WAITING_ROOM_EQUIPMENT = "WaitingRoomEquipment";
    public static final String INPUT_TYPE_WAITING_ROOM_EQUIPMENT = OUTPUT_TYPE_WAITING_ROOM_EQUIPMENT + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_TICKETING_EQUIPMENT = "TicketingEquipment";
    public static final String INPUT_TYPE_TICKETING_EQUIPMENT = OUTPUT_TYPE_TICKETING_EQUIPMENT + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_SANITARY_EQUIPMENT = "SanitaryEquipment";
    public static final String INPUT_TYPE_SANITARY_EQUIPMENT = OUTPUT_TYPE_SANITARY_EQUIPMENT + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_GENERAL_SIGN_EQUIPMENT = "GeneralSign";
    public static final String INPUT_TYPE_GENERAL_SIGN_EQUIPMENT = OUTPUT_TYPE_GENERAL_SIGN_EQUIPMENT + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_KEY_VALUES = "KeyValues";
    public static final String INPUT_TYPE_KEY_VALUES = OUTPUT_TYPE_KEY_VALUES + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_STOPPLACE = "StopPlace";
    public static final String INPUT_TYPE_STOPPLACE = OUTPUT_TYPE_STOPPLACE + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_QUAY = "Quay";
    public static final String INPUT_TYPE_QUAY = OUTPUT_TYPE_QUAY + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_GEO_JSON = "GeoJSON";
    public static final String INPUT_TYPE_GEO_JSON = OUTPUT_TYPE_GEO_JSON + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_PARKING = "Parking";
    public static final String INPUT_TYPE_PARKING = OUTPUT_TYPE_PARKING + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_PARKING_PROPERTIES = "ParkingProperties";
    public static final String INPUT_TYPE_PARKING_PROPERTIES = OUTPUT_TYPE_PARKING_PROPERTIES + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_PARKING_CAPACITY = "ParkingCapacity";
    public static final String INPUT_TYPE_PARKING_CAPACITY = OUTPUT_TYPE_PARKING_CAPACITY + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_PARKING_AREA = "ParkingArea";
    public static final String INPUT_TYPE_PARKING_AREA = OUTPUT_TYPE_PARKING_AREA + INPUT_TYPE_POSTFIX;

    public static final String GEOMETRY_TYPE_ENUM = "GeoJSONType";

    public static final String LIMITATION_STATUS_ENUM = "LimitationStatusType";

    public static final String STOP_PLACE_TYPE_ENUM = "StopPlaceType";
    public static final String INTERCHANGE_WEIGHTING_TYPE_ENUM = "InterchangeWeightingType";

    public static final String PARKING_TYPE_ENUM = "ParkingType";

    public static final String PARKING_VEHICLE_ENUM = "ParkingVehicleType";
    public static final String PARKING_LAYOUT_ENUM = "ParkingLayoutType";
    public static final String PARKING_RESERVATION_ENUM = "ParkingReservationType";
    public static final String PARKING_USER_ENUM = "ParkingUserType";
    public static final String PARKING_STAY_TYPE_ENUM = "ParkingStayType";

    public static final String ID = "id";
    public static final String STOP_PLACE_TYPE = "stopPlaceType";
    public static final String SHORT_NAME = "shortName";
    public static final String DESCRIPTION = "description";
    public static final String ACCESSIBILITY_ASSESSMENT = "accessibilityAssessment";
    public static final String LIMITATIONS = "limitations";
    public static final String MOBILITY_IMPAIRED_ACCESS = "mobilityImpairedAccess";

    public static final String WHEELCHAIR_ACCESS = "wheelchairAccess";
    public static final String STEP_FREE_ACCESS = "stepFreeAccess";
    public static final String ESCALATOR_FREE_ACCESS = "escalatorFreeAccess";
    public static final String LIFT_FREE_ACCESS = "liftFreeAccess";
    public static final String AUDIBLE_SIGNALS_AVAILABLE = "audibleSignalsAvailable";

    public static final String PLACE_EQUIPMENTS = "placeEquipments";
    public static final String TICKETING_EQUIPMENT = "ticketingEquipment";
    public static final String SANITARY_EQUIPMENT = "sanitaryEquipment";
    public static final String SHELTER_EQUIPMENT = "shelterEquipment";
    public static final String CYCLE_STORAGE_EQUIPMENT = "cycleStorageEquipment";
    public static final String WAITING_ROOM_EQUIPMENT = "waitingRoomEquipment";
    public static final String GENERAL_SIGN = "generalSign";

    // SanitaryEquipment
    public static final String NUMBER_OF_TOILETS = "numberOfToilets";
    public static final String GENDER = "gender";

    // WaitingRoomEquipment
    public static final String SEATS = "seats";
    public static final String STEP_FREE = "stepFree";
    public static final String HEATED = "heated";

    // ShelterEquipment
    public static final String ENCLOSED = "enclosed";


    // TicketingEquipment
    public static final String TICKET_MACHINES = "ticketMachines";
    public static final String TICKET_OFFICE = "ticketOffice";
    public static final String NUMBER_OF_MACHINES = "numberOfMachines";

    //CycleStorageEquipment
    public static final String NUMBER_OF_SPACES = "numberOfSpaces";
    public static final String CYCLE_STORAGE_TYPE = "cycleStorageType";
    //GeneralSign


    public static final String OUTPUT_TYPE_PRIVATE_CODE = "PrivateCode";
    public static final String PRIVATE_CODE = "privateCode";
    public static final String INPUT_TYPE_PRIVATE_CODE = OUTPUT_TYPE_PRIVATE_CODE + INPUT_TYPE_POSTFIX;

    public static final String CONTENT = "content";
    public static final String SIGN_CONTENT_TYPE = "signContentType";

    public static final String IMPORTED_ID = "importedId";
    public static final String KEY_VALUES = "keyValues";
    public static final String KEY = "key";
    public static final String VALUES = "values";
    public static final String VERSION = "version";
    public static final String VERSION_COMMENT = "versionComment";
    public static final String PUBLIC_CODE = "publicCode";
    public static final String WEIGHTING = "weighting";

    public static final String IMPORTED_ID_QUERY = "importedId";
    public static final String COUNTY_REF = "countyReference";
    public static final String MUNICIPALITY_REF = "municipalityReference";
    public static final String QUERY = "query";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String ALL_VERSIONS = "allVersions";

    public static final String LONGITUDE_MIN = "lonMin";
    public static final String LATITUDE_MIN = "latMin";
    public static final String LONGITUDE_MAX = "lonMax";
    public static final String LATITUDE_MAX = "latMax";

    public static final String GEOMETRY = "geometry";
    public static final String TYPE = "type";
    public static final String COORDINATES = "coordinates";
    public static final String IGNORE_STOPPLACE_ID = "ignoreStopPlaceId";
    public static final String QUAYS = "quays";
    public static final String COMPASS_BEARING = "compassBearing";
    public static final String VALID_BETWEENS = "validBetweens";
    public static final String ADDRESSABLE_PLACE = "addressablePlace";
    public static final String PLACE_REF = "placeRef";

    public static final String NAME = "name";
    public static final String NAME_TYPE = "nameType";
    public static final String ALTERNATIVE_NAMES = "alternativeNames";
    public static final String TOPOGRAPHIC_PLACE_TYPE_ENUM = "TopographicPlaceType";
    public static final String TOPOGRAPHIC_PLACE_TYPE = "topographicPlaceType";
    public static final String TOPOGRAPHIC_PLACE = "topographicPlace";
    public static final String PARENT_TOPOGRAPHIC_PLACE = "parentTopographicPlace";
    public static final String VALUE = "value";
    public static final String LANG = "lang";

    public static final String TOTAL_CAPACITY = "totalCapacity";


    public static final String PARKING_TYPE = "parkingType";
    public static final String PARKING_VEHICLE_TYPES = "parkingVehicleTypes";
    public static final String PARKING_VEHICLE_TYPE = "parkingVehicleType";
    public static final String PARKING_USER_TYPES = "parkingUserTypes";
    public static final String MAXIMUM_STAY = "maximumStay";
    public static final String PARKING_STAY_TYPE = "parkingStayType";
    public static final String SPACES = "spaces";
    public static final String PARKING_PROPERTIES = "parkingProperties";
    public static final String PARKING_LAYOUT = "parkingLayout";
    public static final String PARKING_AREAS = "parkingAreas";
    public static final String PRINCIPAL_CAPACITY = "principalCapacity";
    public static final String OVERNIGHT_PARKING_PERMITTED = "overnightParkingPermitted";
    public static final String RECHARGING_AVAILABLE = "rechargingAvailable";
    public static final String SECURE = "secure";
    public static final String REAL_TIME_OCCUPANCY_AVAILABLE = "realTimeOccupancyAvailable";
    public static final String PARKING_RESERVATION = "parkingReservation";
    public static final String BOOKING_URL = "bookingUrl";
    public static final String FREE_PARKING_OUT_OF_HOURS = "freeParkingOutOfHours";
    public static final String LABEL = "label";
    public static final String PARENT_SITE_REF = "parentSiteRef";

    public static final String FIND_STOPPLACE = "stopPlace";
    public static final String FIND_STOPPLACE_BY_BBOX = "stopPlaceBBox";
    public static final String FIND_TOPOGRAPHIC_PLACE = "topographicPlace";
    public static final String FIND_PATH_LINK = "pathLink";
    public static final String FIND_PARKING = "parking";

    public static final String FIND_BY_STOP_PLACE_ID = "stopPlaceId";

    public static final String MUTATE_STOPPLACE = "mutateStopPlace";
    public static final String MUTATE_PATH_LINK = "mutatePathlink";
    public static final String MUTATE_PARKING = "mutateParking";


    public static final String QUAY_ID = "quayId";
    public static final String STOP_PLACE_ID = "stopPlaceId";
    public static final String FROM_STOP_PLACE_ID = "fromStopPlaceId";
    public static final String TO_STOP_PLACE_ID = "toStopPlaceId";
    public static final String FROM_QUAY_ID = "fromQuayId";
    public static final String TO_QUAY_ID = "toQuayId";
    public static final String MERGE_STOP_PLACES = "mergeStopPlaces";
    public static final String MERGE_QUAYS = "mergeQuays";
    public static final String MOVE_QUAYS_TO_STOP = "moveQuaysToStop";
    public static final String QUAY_IDS = "quayIds";
    public static final String DELETE_STOP_PLACE = "deleteStopPlace";
    public static final String DELETE_QUAY_FROM_STOP_PLACE = "deleteQuay";
}
