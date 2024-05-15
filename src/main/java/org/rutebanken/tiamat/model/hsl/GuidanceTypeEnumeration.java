package org.rutebanken.tiamat.model.hsl;

public enum GuidanceTypeEnumeration {
    BRAILLE("braille"),
    NONE("none"),
    OTHER("other");
    private final String value;

    GuidanceTypeEnumeration(String v) {
        value = v;
    }

    public static GuidanceTypeEnumeration fromValue(String value) {
        for (GuidanceTypeEnumeration enumeration : GuidanceTypeEnumeration.values()) {
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
