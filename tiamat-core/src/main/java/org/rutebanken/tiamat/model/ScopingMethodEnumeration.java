package org.rutebanken.tiamat.model;

public enum ScopingMethodEnumeration {
    EXPLICIT_STOPS("explicitStops"),
    IMPLICIT_SPATIAL_PROJECTION("implicitSpatialProjection"),
    EXPLICIT_PERIPHERY_STOPS("explicitPeripheryStops"),
    OTHER("other");

    private final String value;

    ScopingMethodEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ScopingMethodEnumeration fromValue(String v) {

        for (ScopingMethodEnumeration c : ScopingMethodEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
