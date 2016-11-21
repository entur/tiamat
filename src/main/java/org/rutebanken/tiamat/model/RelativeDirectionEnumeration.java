package org.rutebanken.tiamat.model;

public enum RelativeDirectionEnumeration {

    BOTH("both"),
    FORWARDS("forwards"),
    BACKWARDS("backwards");
    private final String value;

    RelativeDirectionEnumeration(String v) {
        value = v;
    }

    public static RelativeDirectionEnumeration fromValue(String v) {
        for (RelativeDirectionEnumeration c : RelativeDirectionEnumeration.values()) {
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
