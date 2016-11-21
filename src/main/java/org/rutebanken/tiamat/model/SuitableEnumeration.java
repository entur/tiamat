package org.rutebanken.tiamat.model;

public enum SuitableEnumeration {

    SUITABLE("suitable"),
    NOT_SUITABLE("notSuitable");
    private final String value;

    SuitableEnumeration(String v) {
        value = v;
    }

    public static SuitableEnumeration fromValue(String v) {
        for (SuitableEnumeration c : SuitableEnumeration.values()) {
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
