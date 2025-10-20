
package org.rutebanken.tiamat.model.vehicle;

public enum AllPublicTransportModesEnumeration {
    ALL("all"),
    UNKNOWN("unknown"),
    BUS("bus"),
    TROLLEY_BUS("trolleyBus"),
    TRAM("tram"),
    COACH("coach"),
    RAIL("rail"),
    INTERCITY_RAIL("intercityRail"),
    URBAN_RAIL("urbanRail"),
    METRO("metro"),
    AIR("air"),
    WATER("water"),
    CABLEWAY("cableway"),
    FUNICULAR("funicular"),
    SNOW_AND_ICE("snowAndIce"),
    TAXI("taxi"),
    FERRY("ferry"),
    LIFT("lift"),
    SELF_DRIVE("selfDrive"),
    ANY_MODE("anyMode"),
    OTHER("other");

    private final String value;

    private AllPublicTransportModesEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static AllPublicTransportModesEnumeration fromValue(String v) {
        for(AllPublicTransportModesEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
