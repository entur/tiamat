package org.rutebanken.tiamat.model.vehicle;

public enum VehicleOrientationEnumeration {
    FORWARDS("forwards"),
    BACKWARDS("backwards"),
    UNKNOWN("unknown");

    private final String value;

    private VehicleOrientationEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static VehicleOrientationEnumeration fromValue(String v) {
        for(VehicleOrientationEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
