package org.rutebanken.tiamat.model;

public enum StopTypeEnumeration {

    ONSTREET_BUS("onstreetBus"),
    ONSTREET_TRAM("onstreetTram"),
    AIRPORT("airport"),
    RAIL_STATION("railStation"),
    METRO_STATION("metroStation"),
    BUS_STATION("busStation"),
    COACH_STATION("coachStation"),
    TRAM_STATION("tramStation"),
    HARBOUR_PORT("harbourPort"),
    FERRY_PORT("ferryPort"),
    FERRY_STOP("ferryStop"),
    LIFT_STATION("liftStation"),
    VEHICLE_RAIL_INTERCHANGE("vehicleRailInterchange"),
    OTHER("other");
    private final String value;

    StopTypeEnumeration(String v) {
        value = v;
    }

    public static StopTypeEnumeration fromValue(String value) {
        for (StopTypeEnumeration stopTypeEnumeration : StopTypeEnumeration.values()) {
            if (stopTypeEnumeration.value.equals(value)) {
                return stopTypeEnumeration;
            }
        }
        throw new IllegalArgumentException(value);
    }

    public String value() {
        return value;
    }

}
