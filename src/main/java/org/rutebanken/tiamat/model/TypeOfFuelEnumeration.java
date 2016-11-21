package org.rutebanken.tiamat.model;

public enum TypeOfFuelEnumeration {

    PETROL("petrol"),
    DIESEL("diesel"),
    NATURAL_GAS("naturalGas"),
    BIODIESEL("biodiesel"),
    ELECTRICITY("electricity"),
    OTHER("other");
    private final String value;

    TypeOfFuelEnumeration(String v) {
        value = v;
    }

    public static TypeOfFuelEnumeration fromValue(String v) {
        for (TypeOfFuelEnumeration c : TypeOfFuelEnumeration.values()) {
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
