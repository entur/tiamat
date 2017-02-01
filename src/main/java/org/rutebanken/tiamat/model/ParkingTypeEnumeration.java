package org.rutebanken.tiamat.model;

public enum ParkingTypeEnumeration {

    PARK_AND_RIDE("parkAndRide"),
    LIFT_SHARE_PARKING("liftShareParking"),
    URBAN_PARKING("urbanParking"),
    AIRPORT_PARKING("airportParking"),
    TRAIN_STATION_PARKING("trainStationParking"),
    EXHIBITION_CENTRE_PARKING("exhibitionCentreParking"),
    RENTAL_CAR_PARKING("rentalCarParking"),
    SHOPPING_CENTRE_PARKING("shoppingCentreParking"),
    MOTORWAY_PARKING("motorwayParking"),
    ROADSIDE("roadside"),
    PARKING_ZONE("parkingZone"),
    UNDEFINED("undefined"),
    CYCLE_RENTAL("cycleRental"),
    OTHER("other");
    private final String value;

    ParkingTypeEnumeration(String v) {
        value = v;
    }

    public static ParkingTypeEnumeration fromValue(String v) {
        for (ParkingTypeEnumeration c : ParkingTypeEnumeration.values()) {
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
