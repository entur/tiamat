package org.rutebanken.tiamat.model;

public enum CongestionEnumeration {

    NO_WAITING("noWaiting"),
    QUEUE("queue"),
    CROWDING("crowding"),
    FULL("full");
    private final String value;

    CongestionEnumeration(String v) {
        value = v;
    }

    public static CongestionEnumeration fromValue(String v) {
        for (CongestionEnumeration c : CongestionEnumeration.values()) {
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
