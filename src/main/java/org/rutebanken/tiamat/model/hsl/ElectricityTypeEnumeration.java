package org.rutebanken.tiamat.model.hsl;

public enum ElectricityTypeEnumeration {
    CONTINUOUS("continuous"),
    LIGHT("light"),
    CONTINUOUS_UNDER_CONSTRUCTION("continuousUnderConstruction"),
    CONTINUOUS_PLANNED("continuousPlanned"),
    TEMPORARILY_OFF("temporarilyOff"),
    NONE("none");

    private final String value;

    ElectricityTypeEnumeration(String v) {
        value = v;
    }

    public static ElectricityTypeEnumeration fromValue(String value) {
        for (ElectricityTypeEnumeration enumeration : ElectricityTypeEnumeration.values()) {
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
