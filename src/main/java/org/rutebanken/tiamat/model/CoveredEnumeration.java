package org.rutebanken.tiamat.model;

public enum CoveredEnumeration {

    INDOORS("indoors"),
    OUTDOORS("outdoors"),
    COVERED("covered"),
    MIXED("mixed"),
    UNKNOWN("unknown");
    private final String value;

    CoveredEnumeration(String v) {
        value = v;
    }

    public static CoveredEnumeration fromValue(String v) {
        for (CoveredEnumeration c : CoveredEnumeration.values()) {
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
