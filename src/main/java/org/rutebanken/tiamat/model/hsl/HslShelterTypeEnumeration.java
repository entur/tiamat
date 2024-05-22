package org.rutebanken.tiamat.model.hsl;

// Note: named with Hsl prefix to avoid colliding with ShelterTypeEnumeration
public enum HslShelterTypeEnumeration {
    GLASS("glass"),
    STEEL("steel"),
    POST("post"),
    VIRTUAL("virtual"),
    LEAVE_OFF("leaveOff");

    private final String value;

    HslShelterTypeEnumeration(String v) {
        value = v;
    }

    public static HslShelterTypeEnumeration fromValue(String value) {
        for (HslShelterTypeEnumeration enumeration : HslShelterTypeEnumeration.values()) {
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
