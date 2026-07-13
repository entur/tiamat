package org.rutebanken.tiamat.model;

public enum ZoneTopologyEnumeration {
    OVERLAPPING("overlapping"),
    HONEYCOMB("honeycomb"),
    RING("ring"),
    ANNULAR("annular"),
    NESTED("nested"),
    TILED("tiled"),
    SEQUENCE("sequence"),
    OVERLAPPING_SEQUENCE("overlappingSequence"),
    OTHER("other");

    private final String value;

    ZoneTopologyEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static ZoneTopologyEnumeration fromValue(String v) {

        for (ZoneTopologyEnumeration c : ZoneTopologyEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
