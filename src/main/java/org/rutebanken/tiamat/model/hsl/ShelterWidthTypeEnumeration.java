package org.rutebanken.tiamat.model.hsl;

public enum ShelterWidthTypeEnumeration {
    WIDE("wide"),
    NARROW("narrow"),
    OTHER("other");
    private final String value;

    ShelterWidthTypeEnumeration(String v) {
        value = v;
    }

    public static ShelterWidthTypeEnumeration fromValue(String value) {
        for (ShelterWidthTypeEnumeration enumeration : ShelterWidthTypeEnumeration.values()) {
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
