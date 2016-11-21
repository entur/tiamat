package org.rutebanken.tiamat.model;

public enum ZoneTopologyEnumeration {

    OVERLAPPING("overlapping"),
    HONEYCOMB("honeycomb"),
    RING("ring"),
    NESTED("nested"),
    OTHER("other");
    private final String value;

    ZoneTopologyEnumeration(String v) {
        value = v;
    }

    public static ZoneTopologyEnumeration fromValue(String v) {
        for (ZoneTopologyEnumeration c : ZoneTopologyEnumeration.values()) {
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
