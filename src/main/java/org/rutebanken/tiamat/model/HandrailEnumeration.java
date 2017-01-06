package org.rutebanken.tiamat.model;

public enum HandrailEnumeration {

    NONE("none"),
    ONE_SIDE("oneSide"),
    BOTH_SIDES("bothSides");
    private final String value;

    HandrailEnumeration(String v) {
        value = v;
    }

    public static HandrailEnumeration fromValue(String v) {
        for (HandrailEnumeration c : HandrailEnumeration.values()) {
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
