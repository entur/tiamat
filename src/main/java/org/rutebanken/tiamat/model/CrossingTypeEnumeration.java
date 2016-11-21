package org.rutebanken.tiamat.model;

public enum CrossingTypeEnumeration {

    LEVEL_CROSSING("levelCrossing"),
    BARROW_CROSSING("barrowCrossing"),
    ROAD_CROSSING("roadCrossing"),
    ROAD_CROSSING_WITH_ISLAND("roadCrossingWithIsland"),
    OTHER("other");
    private final String value;

    CrossingTypeEnumeration(String v) {
        value = v;
    }

    public static CrossingTypeEnumeration fromValue(String v) {
        for (CrossingTypeEnumeration c : CrossingTypeEnumeration.values()) {
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
