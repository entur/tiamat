package org.rutebanken.tiamat.model;

public enum FlexibleLinkTypeEnumeration {

    HAIL_AND_RIDE("hailAndRide"),
    ON_DEMAND("onDemand"),
    FIXED("fixed"),
    OTHER("other");
    private final String value;

    FlexibleLinkTypeEnumeration(String v) {
        value = v;
    }

    public static FlexibleLinkTypeEnumeration fromValue(String v) {
        for (FlexibleLinkTypeEnumeration c : FlexibleLinkTypeEnumeration.values()) {
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
