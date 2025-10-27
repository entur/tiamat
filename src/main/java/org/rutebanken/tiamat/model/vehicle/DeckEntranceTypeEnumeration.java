package org.rutebanken.tiamat.model.vehicle;

public enum DeckEntranceTypeEnumeration {
    EXTERNAL("external"),
    COMMUNICATING("communicating"),
    INTERNAL("internal");

    private final String value;

    private DeckEntranceTypeEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static DeckEntranceTypeEnumeration fromValue(String v) {
        for(DeckEntranceTypeEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
