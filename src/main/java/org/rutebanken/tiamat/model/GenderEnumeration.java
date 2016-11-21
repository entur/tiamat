package org.rutebanken.tiamat.model;

public enum GenderEnumeration {

    FEMALE("female"),
    MALE("male");
    private final String value;

    GenderEnumeration(String v) {
        value = v;
    }

    public static GenderEnumeration fromValue(String v) {
        for (GenderEnumeration c : GenderEnumeration.values()) {
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
