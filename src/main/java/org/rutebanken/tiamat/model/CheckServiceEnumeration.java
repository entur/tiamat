package org.rutebanken.tiamat.model;

public enum CheckServiceEnumeration {

    SELF_SERVICE("selfService"),
    COUNTER_SERVICE("counterService"),
    ANY_SERVICE("anyService"),
    OTHER("other");
    private final String value;

    CheckServiceEnumeration(String v) {
        value = v;
    }

    public static CheckServiceEnumeration fromValue(String v) {
        for (CheckServiceEnumeration c : CheckServiceEnumeration.values()) {
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
