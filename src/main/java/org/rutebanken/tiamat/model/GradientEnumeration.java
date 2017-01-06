package org.rutebanken.tiamat.model;

public enum GradientEnumeration {

    VERY_STEEP("verySteep"),
    STEEP("steep"),
    MEDIUM("medium"),
    GENTLE("gentle"),
    LEVEL("level");
    private final String value;

    GradientEnumeration(String v) {
        value = v;
    }

    public static GradientEnumeration fromValue(String v) {
        for (GradientEnumeration c : GradientEnumeration.values()) {
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
