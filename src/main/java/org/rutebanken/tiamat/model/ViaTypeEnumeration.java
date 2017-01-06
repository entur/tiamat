package org.rutebanken.tiamat.model;

public enum ViaTypeEnumeration {

    STOP_POINT("stopPoint"),
    NAME("name"),
    OTHER("other");
    private final String value;

    ViaTypeEnumeration(String v) {
        value = v;
    }

    public static ViaTypeEnumeration fromValue(String v) {
        for (ViaTypeEnumeration c : ViaTypeEnumeration.values()) {
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
