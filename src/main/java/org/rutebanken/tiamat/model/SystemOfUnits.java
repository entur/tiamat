package org.rutebanken.tiamat.model;

public enum SystemOfUnits {


    SI_METRES("SiMetres"),

    SI_KILOMETERS_AND_METRES("SiKilometersAndMetres"),
    OTHER("Other");
    private final String value;

    SystemOfUnits(String v) {
        value = v;
    }

    public static SystemOfUnits fromValue(String v) {
        for (SystemOfUnits c : SystemOfUnits.values()) {
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
