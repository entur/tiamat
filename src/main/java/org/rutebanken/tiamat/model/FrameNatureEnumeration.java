package org.rutebanken.tiamat.model;

public enum FrameNatureEnumeration {

    PLANNED("planned"),
    OPERATIONAL("operational"),
    CONTINGENCY_PLAN("contingencyPlan"),
    OTHER("other");
    private final String value;

    FrameNatureEnumeration(String v) {
        value = v;
    }

    public static FrameNatureEnumeration fromValue(String v) {
        for (FrameNatureEnumeration c : FrameNatureEnumeration.values()) {
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
