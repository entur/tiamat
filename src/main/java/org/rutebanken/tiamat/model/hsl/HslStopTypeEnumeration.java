package org.rutebanken.tiamat.model.hsl;

// Note: named with Hsl prefix to avoid colliding with StopTypeEnumeration
public enum HslStopTypeEnumeration {
    PULL_OUT("pullOut"), // Syvennys
    BUS_BULB("busBulb"), // Uloke
    IN_LANE("inLane"), // Ajoradalla
    OTHER("other");
    private final String value;

    HslStopTypeEnumeration(String v) {
        value = v;
    }

    public static HslStopTypeEnumeration fromValue(String value) {
        for (HslStopTypeEnumeration enumeration : HslStopTypeEnumeration.values()) {
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
