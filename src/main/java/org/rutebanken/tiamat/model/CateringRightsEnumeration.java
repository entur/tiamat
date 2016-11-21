package org.rutebanken.tiamat.model;

public enum CateringRightsEnumeration {

    MEAL_INCLUDED("mealIncluded"),
    MEAL_INCLUDED_FOR_FIRST_CLASS_PASSENGERS("mealIncludedForFirstClassPassengers"),
    NO_MEAL_INCLUDED("noMealIncluded");
    private final String value;

    CateringRightsEnumeration(String v) {
        value = v;
    }

    public static CateringRightsEnumeration fromValue(String v) {
        for (CateringRightsEnumeration c : CateringRightsEnumeration.values()) {
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
