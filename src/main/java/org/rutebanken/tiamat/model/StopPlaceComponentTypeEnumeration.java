package org.rutebanken.tiamat.model;

public enum StopPlaceComponentTypeEnumeration {

    QUAY("quay"),
    ACCESS_SPACE("accessSpace"),
    ENTRANCE("entrance"),
    BOARDING_POSITION("boardingPosition"),
    STOPPING_PLACE("stoppingPlace");
    private final String value;

    StopPlaceComponentTypeEnumeration(String v) {
        value = v;
    }

    public static StopPlaceComponentTypeEnumeration fromValue(String v) {
        for (StopPlaceComponentTypeEnumeration c : StopPlaceComponentTypeEnumeration.values()) {
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
