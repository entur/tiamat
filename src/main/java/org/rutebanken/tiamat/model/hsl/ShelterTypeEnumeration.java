package org.rutebanken.tiamat.model.hsl;

public enum ShelterTypeEnumeration {
    GLASS("glass"),
    STEEL("steel"),
    POST("post"),
    VIRTUAL("virtual"),
    LEAVE_OFF("leaveOff");

    private final String value;

    ShelterTypeEnumeration(String v) {
        value = v;
    }

    public static ShelterTypeEnumeration fromValue(String value) {
        for (ShelterTypeEnumeration enumeration : ShelterTypeEnumeration.values()) {
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
