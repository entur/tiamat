package org.rutebanken.tiamat.model;

public enum StopUseEnumeration {


    ACCESS("access"),

    INTERCHANGE_ONLY("interchangeOnly"),

    PASSTHROUGH("passthrough"),
    NO_BOARDING_OR_ALIGHTING("noBoardingOrAlighting");
    private final String value;

    StopUseEnumeration(String v) {
        value = v;
    }

    public static StopUseEnumeration fromValue(String v) {
        for (StopUseEnumeration c : StopUseEnumeration.values()) {
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
