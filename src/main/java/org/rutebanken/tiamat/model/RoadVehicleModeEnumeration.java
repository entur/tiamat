package org.rutebanken.tiamat.model;

public enum RoadVehicleModeEnumeration {

    BUS("bus"),
    COACH("coach"),
    TROLLEY_BUS("trolleyBus"),
    TRAM("tram");
    private final String value;

    RoadVehicleModeEnumeration(String v) {
        value = v;
    }

    public static RoadVehicleModeEnumeration fromValue(String v) {
        for (RoadVehicleModeEnumeration c : RoadVehicleModeEnumeration.values()) {
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
