package org.rutebanken.tiamat.rest.graphql;

public class GraphQLNames {

    private static final String INPUT_TYPE_POSTFIX = "Input";

    public static final String OUTPUT_TYPE_TOPOGRAPHIC_PLACE = "TopographicPlace";
    public static final String INPUT_TYPE_TOPOGRAPHIC_PLACE = OUTPUT_TYPE_TOPOGRAPHIC_PLACE + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_PATH_LINK = "PathLink";
    public static final String INPUT_TYPE_PATH_LINK = OUTPUT_TYPE_PATH_LINK + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_PATH_LINK_END = "PathLinkEnd";
    public static final String INPUT_TYPE_PATH_LINK_END = OUTPUT_TYPE_PATH_LINK_END + INPUT_TYPE_POSTFIX;

    public static final String PATH_LINK_FROM = "from";
    public static final String PATH_LINK_TO = "to";

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

    public static final String OUTPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING = "EmbeddableMultilingualString";
    public static final String INPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING = OUTPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_STOPPLACE = "StopPlace";
    public static final String INPUT_TYPE_STOPPLACE = OUTPUT_TYPE_STOPPLACE + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_QUAY = "Quay";
    public static final String INPUT_TYPE_QUAY = OUTPUT_TYPE_QUAY + INPUT_TYPE_POSTFIX;

    public static final String OUTPUT_TYPE_GEO_JSON = "GeoJSON";
    public static final String INPUT_TYPE_GEO_JSON = OUTPUT_TYPE_GEO_JSON + INPUT_TYPE_POSTFIX;

    public static final String STOP_PLACE_TYPE_ENUM = "StopPlaceType";

    public static final String ID = "id";
    public static final String STOP_PLACE_TYPE = "stopPlaceType";
    public static final String SHORT_NAME = "shortName";
    public static final String DESCRIPTION = "description";
    public static final String IMPORTED_ID = "importedId";
    public static final String VERSION = "version";
    public static final String PUBLIC_CODE = "publicCode";
    public static final String ALL_AREAS_WHEELCHAIR_ACCESSIBLE = "allAreasWheelchairAccessible";

    public static final String IMPORTED_ID_QUERY = "importedId";
    public static final String COUNTY_REF = "countyReference";
    public static final String MUNICIPALITY_REF = "municipalityReference";
    public static final String QUERY = "query";
    public static final String PAGE = "page";
    public static final String SIZE = "size";

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

    public static final String NAME = "name";
    public static final String TOPOGRAPHIC_PLACE_TYPE_ENUM = "TopographicPlaceType";
    public static final String TOPOGRAPHIC_PLACE_TYPE = "topographicPlaceType";
    public static final String TOPOGRAPHIC_PLACE = "topographicPlace";
    public static final String PARENT_TOPOGRAPHIC_PLACE = "parentTopographicPlace";
    public static final String VALUE = "value";
    public static final String LANG = "lang";

    public static final String FIND_STOPPLACE = "stopPlace";
    public static final String FIND_STOPPLACE_BY_BBOX = "stopPlaceBBox";
    public static final String FIND_TOPOGRAPHIC_PLACE = "topographicPlace";
    public static final String FIND_PATH_LINK = "pathLink";

    public static final String FIND_BY_STOP_PLACE_ID = "stopPlaceId";

    public static final String MUTATE_STOPPLACE = "mutateStopPlace";
    public static final String MUTATE_PATH_LINK = "mutatePathlink";
}
