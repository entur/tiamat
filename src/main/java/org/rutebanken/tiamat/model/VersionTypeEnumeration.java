package org.rutebanken.tiamat.model;

public enum VersionTypeEnumeration {

    POINT("point"),
    BASELINE("baseline");
    private final String value;

    VersionTypeEnumeration(String v) {
        value = v;
    }

    public static VersionTypeEnumeration fromValue(String v) {
        for (VersionTypeEnumeration c : VersionTypeEnumeration.values()) {
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
