package org.rutebanken.tiamat.model;

public enum QuayTypeEnumeration {

    AIRLINE_GATE("airlineGate"),
    RAIL_PLATFORM("railPlatform"),
    METRO_PLATFORM("metroPlatform"),
    COACH_STOP("coachStop"),
    BUS_STOP("busStop"),
    BUS_BAY("busBay"),
    TRAM_PLATFORM("tramPlatform"),
    TRAM_STOP("tramStop"),
    BOAT_QUAY("boatQuay"),
    FERRY_LANDING("ferryLanding"),
    TELECABINE_PLATFORM("telecabinePlatform"),
    TAXI_STAND("taxiStand"),
    SET_DOWN_PLACE("setDownPlace"),
    VEHICLE_LOADING_PLACE("vehicleLoadingPlace"),
    OTHER("other");
    private final String value;

    QuayTypeEnumeration(String v) {
        value = v;
    }

    public static QuayTypeEnumeration fromValue(String v) {
        for (QuayTypeEnumeration c : QuayTypeEnumeration.values()) {
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
