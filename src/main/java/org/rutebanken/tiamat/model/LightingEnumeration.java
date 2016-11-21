package org.rutebanken.tiamat.model;

public enum LightingEnumeration {

    WELL_LIT("wellLit"),
    POORLY_LIT("poorlyLit"),
    UNLIT("unlit"),
    UNKNOWN("unknown"),
    OTHER("other");
    private final String value;

    LightingEnumeration(String v) {
        value = v;
    }

    public static LightingEnumeration fromValue(String v) {
        for (LightingEnumeration c : LightingEnumeration.values()) {
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
