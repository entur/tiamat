package org.rutebanken.tiamat.rest.graphql;

public class GraphQLNames {

    private static final String INPUT_TYPE_POSTFIX = "Input";

    static final String OUTPUT_TYPE_TOPOGRAPHIC_PLACE = "TopographicPlace";
    static final String INPUT_TYPE_TOPOGRAPHIC_PLACE = OUTPUT_TYPE_TOPOGRAPHIC_PLACE + INPUT_TYPE_POSTFIX;

    static final String OUTPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING = "EmbeddableMultilingualString";
    static final String INPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING = OUTPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING + INPUT_TYPE_POSTFIX;

    static final String OUTPUT_TYPE_STOPPLACE = "StopPlace";
    static final String INPUT_TYPE_STOPPLACE = OUTPUT_TYPE_STOPPLACE + INPUT_TYPE_POSTFIX;

    static final String OUTPUT_TYPE_QUAY = "Quay";
    static final String INPUT_TYPE_QUAY = OUTPUT_TYPE_QUAY + INPUT_TYPE_POSTFIX;

    static final String OUTPUT_TYPE_LOCATION = "Location";
    static final String INPUT_TYPE_LOCATION = OUTPUT_TYPE_LOCATION + INPUT_TYPE_POSTFIX;

    static final String OUTPUT_TYPE_GEO_JSON = "GeoJSON";
    static final String INPUT_TYPE_GEO_JSON = OUTPUT_TYPE_GEO_JSON + INPUT_TYPE_POSTFIX;

    static final String STOP_PLACE_TYPE_ENUM = "StopPlaceType";

    static final String ID = "id";
    static final String STOP_PLACE_TYPE = "stopPlaceType";
    static final String SHORT_NAME = "shortName";
    static final String DESCRIPTION = "description";
    static final String IMPORTED_ID = "importedId";
    static final String VERSION = "version";
    static final String PUBLIC_CODE = "publicCode";
    static final String ALL_AREAS_WHEELCHAIR_ACCESSIBLE = "allAreasWheelchairAccessible";

    static final String IMPORTED_ID_QUERY = "importedId";
    static final String COUNTY_REF = "countyReference";
    static final String MUNICIPALITY_REF = "municipalityReference";
    static final String QUERY = "query";
    static final String PAGE = "page";
    static final String SIZE = "size";

    static final String LONGITUDE_MIN = "lonMin";
    static final String LATITUDE_MIN = "latMin";
    static final String LONGITUDE_MAX = "lonMax";
    static final String LATITUDE_MAX = "latMax";

    static final String GEOMETRY = "geometry";
    static final String TYPE = "type";
    static final String COORDINATES = "coordinates";

    static final String LOCATION = "location";
    static final String LONGITUDE = "longitude";
    static final String LATITUDE = "latitude";

    static final String IGNORE_STOPPLACE_ID = "ignoreStopPlaceId";
    static final String QUAYS = "quays";
    static final String COMPASS_BEARING = "compassBearing";

    static final String NAME = "name";
    static final String TOPOGRAPHIC_PLACE_TYPE_ENUM = "TopographicPlaceType";
    static final String TOPOGRAPHIC_PLACE_TYPE = "topographicPlaceType";
    static final String TOPOGRAPHIC_PLACE = "topographicPlace";
    static final String PARENT_TOPOGRAPHIC_PLACE = "parentTopographicPlace";
    static final String VALUE = "value";
    static final String LANG = "lang";

    static final String FIND_STOPPLACE = "stopPlace";
    static final String FIND_STOPPLACE_BY_BBOX = "stopPlaceBBox";
    static final String FIND_TOPOGRAPHIC_PLACE = "topographicPlace";

    static final String MUTATE_STOPPLACE = "mutateStopPlace";

}
