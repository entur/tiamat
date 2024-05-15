package org.rutebanken.tiamat.model.hsl;

public enum MapTypeEnumeration {
    TACTILE("tactile"),
    NONE("none"),
    OTHER("other");
    private final String value;

    MapTypeEnumeration(String v) {
        value = v;
    }

    public static MapTypeEnumeration fromValue(String value) {
        for (MapTypeEnumeration enumeration : MapTypeEnumeration.values()) {
            if (enumeration.value.equals(value)) {
                return enumeration;
            }
        }
        throw new IllegalArgumentException(value);
    }

    public String value() {
        return value;
    }

}
