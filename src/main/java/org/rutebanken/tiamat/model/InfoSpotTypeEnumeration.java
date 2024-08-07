package org.rutebanken.tiamat.model;

public enum InfoSpotTypeEnumeration {
    STATIC("static"),
    DYNAMIC("dynamic"),
    SOUND_BEACON("sound_beacon");

    private final String value;

    InfoSpotTypeEnumeration(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static InfoSpotTypeEnumeration fromValue(String value) {
        for (var c : InfoSpotTypeEnumeration.values()) {
            if (c.value.equals(value)) {
                return c;
            }
        }

        throw new IllegalArgumentException(value + " is not a valid value of InfoSpotTypeEnumeration");
    }
}
