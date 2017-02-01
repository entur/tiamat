package org.rutebanken.tiamat.model;

public enum TrainSizeEnumeration {

    NORMAL("normal"),
    SHORT("short"),
    LONG("long");
    private final String value;

    TrainSizeEnumeration(String v) {
        value = v;
    }

    public static TrainSizeEnumeration fromValue(String v) {
        for (TrainSizeEnumeration c : TrainSizeEnumeration.values()) {
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
