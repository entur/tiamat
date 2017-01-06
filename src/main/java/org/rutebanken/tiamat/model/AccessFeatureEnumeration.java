package org.rutebanken.tiamat.model;

public enum AccessFeatureEnumeration {

    LIFT("lift"),
    ESCALATOR("escalator"),
    FREIGHT_ELEVATOR("freightElevator"),
    TRAVELATOR("travelator"),
    RAMP("ramp"),
    STAIRS("stairs"),
    SERIES_OF_STAIRS("seriesOfStairs"),
    SHUTTLE("shuttle"),
    CROSSING("crossing"),
    BARRIER("barrier"),
    NARROW_ENTRANCE("narrowEntrance"),
    HALL("hall"),
    CONCOURSE("concourse"),
    CONFINED_SPACE("confinedSpace"),
    QUEUE_MANAGEMENT("queueManagement"),
    NONE("none"),
    UNKNOWN("unknown"),
    OTHER("other"),
    OPEN_SPACE("openSpace"),
    STREET("street"),
    PAVEMENT("pavement"),
    FOOTPATH("footpath"),
    PASSAGE("passage");
    private final String value;

    AccessFeatureEnumeration(String v) {
        value = v;
    }

    public static AccessFeatureEnumeration fromValue(String v) {
        for (AccessFeatureEnumeration c : AccessFeatureEnumeration.values()) {
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
