package org.rutebanken.tiamat.model;

public enum SeasonEnumeration {

    SPRING("Spring"),
    SUMMER("Summer"),
    AUTUMN("Autumn"),
    WINTER("Winter"),
    PERENNIALLY("Perennially");
    private final String value;

    SeasonEnumeration(String v) {
        value = v;
    }

    public static SeasonEnumeration fromValue(String v) {
        for (SeasonEnumeration c : SeasonEnumeration.values()) {
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
