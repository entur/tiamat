package org.rutebanken.tiamat.model.vehicle;

public enum PassengerSpaceTypeEnumeration {
    SEATING_AREA("seatingArea"),
    PASSENGER_CABIN("passengerCabin"),
    VEHICLE_AREA("vehicleArea"),
    LUGGAGE_STORE("luggageStore"),
    CORRIDOR("corridor"),
    RESTAURANT("restaurant"),
    TOILET("toilet"),
    BATHROOM("bathroom"),
    OTHER("other");

    private final String value;

    private PassengerSpaceTypeEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static PassengerSpaceTypeEnumeration fromValue(String v) {
        for(PassengerSpaceTypeEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
