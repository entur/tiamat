package org.rutebanken.tiamat.model;

public enum LimitedUseTypeEnumeration {


    INTERCHANGE_ONLY("interchangeOnly"),

    NO_DIRECT_ROAD_ACCESS("noDirectRoadAccess"),

    LONG_WALK_TO_ACCESS("longWalkToAccess"),

    ISOLATED("isolated"),

    LIMITED_SERVICE("limitedService"),
    OTHER("other");
    private final String value;

    LimitedUseTypeEnumeration(String v) {
        value = v;
    }

    public static LimitedUseTypeEnumeration fromValue(String v) {
        for (LimitedUseTypeEnumeration c : LimitedUseTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String value() {
        return value;
    }

}
