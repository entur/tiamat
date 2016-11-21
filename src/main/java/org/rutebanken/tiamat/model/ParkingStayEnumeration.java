package org.rutebanken.tiamat.model;

public enum ParkingStayEnumeration {

    SHORT_STAY("shortStay"),
    MID_TERM("midTerm"),
    LONG_TERM("longTerm"),
    DROPOFF("dropoff"),
    UNLIMITED("unlimited"),
    OTHER("other"),
    ALL("all");
    private final String value;

    ParkingStayEnumeration(String v) {
        value = v;
    }

    public static ParkingStayEnumeration fromValue(String v) {
        for (ParkingStayEnumeration c : ParkingStayEnumeration.values()) {
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
