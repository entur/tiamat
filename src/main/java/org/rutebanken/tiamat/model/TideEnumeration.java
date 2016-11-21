package org.rutebanken.tiamat.model;

public enum TideEnumeration {

    HIGH_TIDE("HighTide"),
    LOW_TIDE("LowTide"),
    NEAP_TIDE("NeapTide"),
    ALL_TIDES("AllTides");
    private final String value;

    TideEnumeration(String v) {
        value = v;
    }

    public static TideEnumeration fromValue(String v) {
        for (TideEnumeration c : TideEnumeration.values()) {
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
