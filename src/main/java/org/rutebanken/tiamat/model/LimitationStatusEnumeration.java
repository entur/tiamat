package org.rutebanken.tiamat.model;

public enum LimitationStatusEnumeration {


    TRUE("true"),

    FALSE("false"),

    UNKNOWN("unknown"),

    PARTIAL("partial");
    private final String value;

    LimitationStatusEnumeration(String v) {
        value = v;
    }

    public static LimitationStatusEnumeration fromValue(String v) {
        for (LimitationStatusEnumeration c : LimitationStatusEnumeration.values()) {
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
