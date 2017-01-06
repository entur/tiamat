package org.rutebanken.tiamat.model;

public enum TelecabinSubmodeEnumeration {

    UNKNOWN("unknown"),
    UNDEFINED("undefined"),
    TELECABIN("telecabin"),
    CABLE_CAR("cableCar"),
    LIFT("lift"),
    CHAIR_LIFT("chairLift"),
    DRAG_LIFT("dragLift"),
    TELECABIN_LINK("telecabinLink");
    private final String value;

    TelecabinSubmodeEnumeration(String v) {
        value = v;
    }

    public static TelecabinSubmodeEnumeration fromValue(String v) {
        for (TelecabinSubmodeEnumeration c : TelecabinSubmodeEnumeration.values()) {
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
