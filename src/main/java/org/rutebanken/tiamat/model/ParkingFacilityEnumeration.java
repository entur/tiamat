package org.rutebanken.tiamat.model;

public enum ParkingFacilityEnumeration {

    UNKNOWN("unknown"),
    CAR_PARK("carPark"),
    PARK_AND_RIDE_PARK("parkAndRidePark"),
    MOTORCYCLE_PARK("motorcyclePark"),
    CYCLE_PARK("cyclePark"),
    RENTAL_CAR_PARK("rentalCarPark"),
    COACH_PARK("coachPark");
    private final String value;

    ParkingFacilityEnumeration(String v) {
        value = v;
    }

    public static ParkingFacilityEnumeration fromValue(String v) {
        for (ParkingFacilityEnumeration c : ParkingFacilityEnumeration.values()) {
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
