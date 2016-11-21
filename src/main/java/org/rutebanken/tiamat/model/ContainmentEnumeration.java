package org.rutebanken.tiamat.model;

public enum ContainmentEnumeration {


    INLINE("inline"),

    BY_REFERENCE("byReference"),
    BY_VERSIONED_REFERENCE("byVersionedReference"),
    BOTH("both");
    private final String value;

    ContainmentEnumeration(String v) {
        value = v;
    }

    public static ContainmentEnumeration fromValue(String v) {
        for (ContainmentEnumeration c : ContainmentEnumeration.values()) {
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
