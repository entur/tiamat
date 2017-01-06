package org.rutebanken.tiamat.model;

public enum BerthFacilityEnumeration {

    UPPER("upper"),
    LOWER("lower"),
    BOTH("both");
    private final String value;

    BerthFacilityEnumeration(String v) {
        value = v;
    }

    public static BerthFacilityEnumeration fromValue(String v) {
        for (BerthFacilityEnumeration c : BerthFacilityEnumeration.values()) {
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
