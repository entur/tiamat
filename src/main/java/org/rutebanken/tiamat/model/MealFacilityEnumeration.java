package org.rutebanken.tiamat.model;

public enum MealFacilityEnumeration {

    BREAKFAST("breakfast"),
    LUNCH("lunch"),
    DINNER("dinner"),
    SNACK("snack"),
    DRINKS("drinks");
    private final String value;

    MealFacilityEnumeration(String v) {
        value = v;
    }

    public static MealFacilityEnumeration fromValue(String v) {
        for (MealFacilityEnumeration c : MealFacilityEnumeration.values()) {
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
