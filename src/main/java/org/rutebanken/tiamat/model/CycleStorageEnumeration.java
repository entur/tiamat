package org.rutebanken.tiamat.model;

public enum CycleStorageEnumeration {

    RACKS("racks"),
    BARS("bars"),
    RAILINGS("railings"),
    CYCLE_SCHEME("cycleScheme"),
    OTHER("other");
    private final String value;

    CycleStorageEnumeration(String v) {
        value = v;
    }

    public static CycleStorageEnumeration fromValue(String v) {
        for (CycleStorageEnumeration c : CycleStorageEnumeration.values()) {
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
