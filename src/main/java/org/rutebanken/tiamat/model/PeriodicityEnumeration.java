package org.rutebanken.tiamat.model;

public enum PeriodicityEnumeration {

    ANNUAL("annual"),
    QUARTERLY("quarterly"),
    MONTHLY("monthly"),
    WEEKLY("weekly"),
    DAILY("daily");
    private final String value;

    PeriodicityEnumeration(String v) {
        value = v;
    }

    public static PeriodicityEnumeration fromValue(String v) {
        for (PeriodicityEnumeration c : PeriodicityEnumeration.values()) {
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
