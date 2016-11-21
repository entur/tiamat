package org.rutebanken.tiamat.model;

public enum RelationToVehicleEnumeration {

    FRONT_LEFT("frontLeft"),
    FRONT_RIGHT("frontRight"),
    BACK_RIGHT("backRight"),
    DRIVER_LEFT("driverLeft"),
    DRIVER_RIGHT("driverRight");
    private final String value;

    RelationToVehicleEnumeration(String v) {
        value = v;
    }

    public static RelationToVehicleEnumeration fromValue(String v) {
        for (RelationToVehicleEnumeration c : RelationToVehicleEnumeration.values()) {
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
