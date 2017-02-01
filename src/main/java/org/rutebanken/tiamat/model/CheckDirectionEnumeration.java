package org.rutebanken.tiamat.model;

public enum CheckDirectionEnumeration {

    FORWARDS("forwards"),
    BACKWARDS("backwards"),
    BOTH_WAYS("bothWays");
    private final String value;

    CheckDirectionEnumeration(String v) {
        value = v;
    }

    public static CheckDirectionEnumeration fromValue(String v) {
        for (CheckDirectionEnumeration c : CheckDirectionEnumeration.values()) {
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
