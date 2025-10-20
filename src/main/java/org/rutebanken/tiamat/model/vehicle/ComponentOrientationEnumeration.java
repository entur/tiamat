package org.rutebanken.tiamat.model.vehicle;

public enum ComponentOrientationEnumeration {
    FORWARDS("forwards"),
    BACKWARDS("backwards"),
    UNKNOWN("unknown"),
    LEFTWARDS("leftwards"),
    RIGHTWARDS("rightwards"),
    ANGLED_LEFT("angledLeft"),
    ANGLED_RIGHT("angledRight"),
    ANGLED_BACK_LEFT("angledBackLeft"),
    ANGLED_BACK_RIGHT("angledBackRight");

    private final String value;

    private ComponentOrientationEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static ComponentOrientationEnumeration fromValue(String v) {
        for(ComponentOrientationEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
