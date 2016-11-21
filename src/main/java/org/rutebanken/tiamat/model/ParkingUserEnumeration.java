package org.rutebanken.tiamat.model;

public enum ParkingUserEnumeration {

    ALL_USERS("allUsers"),
    STAFF("staff"),
    VISITORS("visitors"),
    REGISTERED_DISABLED("registeredDisabled"),
    REGISTERED("registered"),
    RENTAL("rental"),
    DOCTORS("doctors"),
    RESIDENTS_WITH_PERMITS("residentsWithPermits"),
    RESERVATION_HOLDERS("reservationHolders"),
    EMERGENCY_SERVICES("emergencyServices"),
    OTHER("other"),
    ALL("all");
    private final String value;

    ParkingUserEnumeration(String v) {
        value = v;
    }

    public static ParkingUserEnumeration fromValue(String v) {
        for (ParkingUserEnumeration c : ParkingUserEnumeration.values()) {
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
