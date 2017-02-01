package org.rutebanken.tiamat.model;

public enum GatedEnumeration {

    GATED_AREA("gatedArea"),
    OPEN_AREA("openArea"),
    UNKNOWN("unknown");
    private final String value;

    GatedEnumeration(String v) {
        value = v;
    }

    public static GatedEnumeration fromValue(String v) {
        for (GatedEnumeration c : GatedEnumeration.values()) {
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
