package org.rutebanken.tiamat.model;

public enum DirectionTypeEnumeration {

    INBOUND("inbound"),
    OUTBOUND("outbound"),
    CLOCKWISE("clockwise"),
    ANTICLOCKWISE("anticlockwise");
    private final String value;

    DirectionTypeEnumeration(String v) {
        value = v;
    }

    public static DirectionTypeEnumeration fromValue(String v) {
        for (DirectionTypeEnumeration c : DirectionTypeEnumeration.values()) {
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
