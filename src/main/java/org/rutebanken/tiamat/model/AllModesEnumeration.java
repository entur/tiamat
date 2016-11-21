package org.rutebanken.tiamat.model;

public enum AllModesEnumeration {

    ALL("all"),
    UNKNOWN("unknown"),
    AIR("air"),
    BUS("bus"),
    TROLLEY_BUS("trolleyBus"),
    TRAM("tram"),
    COACH("coach"),
    RAIL("rail"),
    INTERCITY_RAIL("intercityRail"),
    URBAN_RAIL("urbanRail"),
    METRO("metro"),
    WATER("water"),
    CABLEWAY("cableway"),
    FUNICULAR("funicular"),
    TAXI("taxi"),

    SELF_DRIVE("selfDrive"),
    FOOT("foot"),
    BICYCLE("bicycle"),
    MOTORCYCLE("motorcycle"),
    CAR("car"),
    SHUTTLE("shuttle");
    private final String value;

    AllModesEnumeration(String v) {
        value = v;
    }

    public static AllModesEnumeration fromValue(String v) {
        for (AllModesEnumeration c : AllModesEnumeration.values()) {
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
