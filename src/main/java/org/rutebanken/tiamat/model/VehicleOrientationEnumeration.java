package org.rutebanken.tiamat.model;

public enum VehicleOrientationEnumeration {

    FORWARDS("forwards"),
    BACKWARDS("backwards"),
    UNKNOWN("unknown");
    private final String value;

    VehicleOrientationEnumeration(String v) {
        value = v;
    }

    public static VehicleOrientationEnumeration fromValue(String v) {
        for (VehicleOrientationEnumeration c : VehicleOrientationEnumeration.values()) {
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
