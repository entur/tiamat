package org.rutebanken.tiamat.model.hsl;

public enum ShelterConditionEnumeration {
    GOOD("good"),
    MEDIOCRE("mediocre"),
    BAD("bad");

    private final String value;

    ShelterConditionEnumeration(String v) {
        value = v;
    }

    public static ShelterConditionEnumeration fromValue(String value) {
        for (ShelterConditionEnumeration enumeration : ShelterConditionEnumeration.values()) {
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
