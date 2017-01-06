package org.rutebanken.tiamat.model;

public enum MetroSubmodeEnumeration {

    UNKNOWN("unknown"),
    UNDEFINED("undefined"),
    METRO("metro"),
    TUBE("tube"),
    URBAN_RAILWAY("urbanRailway");
    private final String value;

    MetroSubmodeEnumeration(String v) {
        value = v;
    }

    public static MetroSubmodeEnumeration fromValue(String v) {
        for (MetroSubmodeEnumeration c : MetroSubmodeEnumeration.values()) {
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
