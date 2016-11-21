package org.rutebanken.tiamat.model;

public enum InterchangeWeightingEnumeration {

    NO_INTERCHANGE("noInterchange"),
    INTERCHANGE_ALLOWED("interchangeAllowed"),
    RECOMMENDED_INTERCHANGE("recommendedInterchange"),
    PREFERRED_INTERCHANGE("preferredInterchange");
    private final String value;

    InterchangeWeightingEnumeration(String v) {
        value = v;
    }

    public static InterchangeWeightingEnumeration fromValue(String v) {
        for (InterchangeWeightingEnumeration c : InterchangeWeightingEnumeration.values()) {
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
