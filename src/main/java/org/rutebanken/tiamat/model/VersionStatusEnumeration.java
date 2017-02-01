package org.rutebanken.tiamat.model;

public enum VersionStatusEnumeration {

    DRAFT("draft"),
    VERSIONED("versioned"),
    DEPRECATED("deprecated"),
    OTHER("other");
    private final String value;

    VersionStatusEnumeration(String v) {
        value = v;
    }

    public static VersionStatusEnumeration fromValue(String v) {
        for (VersionStatusEnumeration c : VersionStatusEnumeration.values()) {
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
