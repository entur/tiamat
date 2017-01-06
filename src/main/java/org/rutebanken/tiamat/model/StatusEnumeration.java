package org.rutebanken.tiamat.model;

public enum StatusEnumeration {


    ACTIVE("active"),

    INACTIVE("inactive"),

    OTHER("other");
    private final String value;

    StatusEnumeration(String v) {
        value = v;
    }

    public static StatusEnumeration fromValue(String v) {
        for (StatusEnumeration c : StatusEnumeration.values()) {
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
