package org.rutebanken.tiamat.model.vehicle;

public enum VehicleSideEnumeration {
    LEFT_SIDE("leftSide"),
    RIGHT_SIDE("rightSide"),
    FRONT_END("frontEnd"),
    BACK_END("backEnd"),
    INTERNAL("internal"),
    ABOVE("above"),
    BELOW("below");

    private final String value;

    private VehicleSideEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static VehicleSideEnumeration fromValue(String v) {
        for(VehicleSideEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
