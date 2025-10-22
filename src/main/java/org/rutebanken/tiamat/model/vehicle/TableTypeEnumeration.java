package org.rutebanken.tiamat.model.vehicle;

public enum TableTypeEnumeration {
    NONE("none"),
    FIXED_FLAT("fixedFlat"),
    FOLD_DOWN_FLAT("foldDownFlat"),
    SEAT_BACK_FOLDING("seatBackFolding"),
    ARM_REST_FOLDING("armRestFolding"),
    SEAT_CLIPON("seatClipon"),
    OTHER("other"),
    UNKNOWN("unknown");

    private final String value;

    private TableTypeEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static TableTypeEnumeration fromValue(String v) {
        for(TableTypeEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
