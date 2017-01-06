package org.rutebanken.tiamat.model;

public enum ParkingLayoutEnumeration {

    COVERED("covered"),
    OPEN_SPACE("openSpace"),
    MULTISTOREY("multistorey"),
    UNDERGROUND("underground"),
    ROADSIDE("roadside"),
    UNDEFINED("undefined"),
    OTHER("other"),
    CYCLE_HIRE("cycleHire");
    private final String value;

    ParkingLayoutEnumeration(String v) {
        value = v;
    }

    public static ParkingLayoutEnumeration fromValue(String v) {
        for (ParkingLayoutEnumeration c : ParkingLayoutEnumeration.values()) {
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
