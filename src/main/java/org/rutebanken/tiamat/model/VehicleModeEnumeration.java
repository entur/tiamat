package org.rutebanken.tiamat.model;

public enum VehicleModeEnumeration {

    AIR("air"),
    BUS("bus"),
    COACH("coach"),
    FERRY("ferry"),
    METRO("metro"),
    RAIL("rail"),
    TROLLEY_BUS("trolleyBus"),
    TRAM("tram"),
    WATER("water"),
    CABLEWAY("cableway"),
    FUNICULAR("funicular"),
    LIFT("lift"),
    OTHER("other");
    private final String value;

    VehicleModeEnumeration(String v) {
        value = v;
    }

    public static VehicleModeEnumeration fromValue(String v) {
        for (VehicleModeEnumeration c : VehicleModeEnumeration.values()) {
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
