package org.rutebanken.tiamat.model.vehicle;

public enum PropulsionTypeEnumeration {
    COMBUSTION("combustion"),
    ELECTRIC("electric"),
    ELECTRIC_ASSIST("electricAssist"),
    HYBRID("hybrid"),
    HUMAN("human"),
    OTHER("other");

    private final String value;

    private PropulsionTypeEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static PropulsionTypeEnumeration fromValue(String v) {
        for(PropulsionTypeEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
