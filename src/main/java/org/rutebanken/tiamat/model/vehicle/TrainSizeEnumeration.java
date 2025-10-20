package org.rutebanken.tiamat.model.vehicle;

public enum TrainSizeEnumeration {
    NORMAL("normal"),
    SHORT("short"),
    LONG("long");

    private final String value;

    private TrainSizeEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static TrainSizeEnumeration fromValue(String v) {
        for(TrainSizeEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
