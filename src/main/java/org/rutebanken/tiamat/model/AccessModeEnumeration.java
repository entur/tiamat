package org.rutebanken.tiamat.model;

public enum AccessModeEnumeration {

    FOOT("foot"),
    BICYCLE("bicycle"),
    BOAT("boat"),
    CAR("car"),
    TAXI("taxi"),
    SHUTTLE("shuttle");
    private final String value;

    AccessModeEnumeration(String v) {
        value = v;
    }

    public static AccessModeEnumeration fromValue(String v) {
        for (AccessModeEnumeration c : AccessModeEnumeration.values()) {
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
