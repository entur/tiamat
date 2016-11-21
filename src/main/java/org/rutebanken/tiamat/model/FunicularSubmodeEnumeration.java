package org.rutebanken.tiamat.model;

public enum FunicularSubmodeEnumeration {

    UNKNOWN("unknown"),
    FUNICULAR("funicular"),
    STREET_CABLE_CAR("streetCableCar"),
    ALL_FUNICULAR_SERVICES("allFunicularServices"),
    UNDEFINED_FUNICULAR("undefinedFunicular");
    private final String value;

    FunicularSubmodeEnumeration(String v) {
        value = v;
    }

    public static FunicularSubmodeEnumeration fromValue(String v) {
        for (FunicularSubmodeEnumeration c : FunicularSubmodeEnumeration.values()) {
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
