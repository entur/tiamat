package org.rutebanken.tiamat.model;

public enum DirectionOfUseEnumeration {

    UP("up"),
    DOWN("down"),
    BOTH("both");
    private final String value;

    DirectionOfUseEnumeration(String v) {
        value = v;
    }

    public static DirectionOfUseEnumeration fromValue(String v) {
        for (DirectionOfUseEnumeration c : DirectionOfUseEnumeration.values()) {
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
