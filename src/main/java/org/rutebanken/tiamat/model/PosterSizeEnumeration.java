package org.rutebanken.tiamat.model;

public enum PosterSizeEnumeration {
    A3("a3"),
    A4("a4"),
    CM80x120("cm80x120");

    private final String value;

    PosterSizeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PosterSizeEnumeration fromValue(String v) {

        for (PosterSizeEnumeration c : PosterSizeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v + " is not a valid value of PosterSizeEnumeration");
    }
}
