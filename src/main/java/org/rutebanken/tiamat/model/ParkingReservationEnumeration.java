package org.rutebanken.tiamat.model;

public enum ParkingReservationEnumeration {

    RESERVATION_REQUIRED("reservationRequired"),
    RESERVATION_ALLOWED("reservationAllowed"),
    NO_RESERVATIONS("noReservations"),
    REGISTRATION_REQUIRED("registrationRequired"),
    OTHER("other");
    private final String value;

    ParkingReservationEnumeration(String v) {
        value = v;
    }

    public static ParkingReservationEnumeration fromValue(String v) {
        for (ParkingReservationEnumeration c : ParkingReservationEnumeration.values()) {
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
