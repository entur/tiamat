package org.rutebanken.tiamat.model;

public enum CateringServiceEnumeration {

    BAR("bar"),
    BEVERAGE_VENDING_MACHINE("beverageVendingMachine"),
    BUFFET("buffet"),
    COFFEE_SHOP("coffeeShop"),
    FIRST_CLASS_RESTAURANT("firstClassRestaurant"),
    FOOD_VENDING_MACHINE("foodVendingMachine"),
    HOT_FOOD_SERVICE("hotFoodService"),
    RESTAURANT("restaurant"),
    SNACKS("snacks"),
    TROLLEY_SERVICE("trolleyService"),
    NO_BEVERAGES_AVAILABLE("noBeveragesAvailable"),
    NO_FOOD_SERVICE_AVAILABLE("noFoodServiceAvailable"),
    OTHER("other");
    private final String value;

    CateringServiceEnumeration(String v) {
        value = v;
    }

    public static CateringServiceEnumeration fromValue(String v) {
        for (CateringServiceEnumeration c : CateringServiceEnumeration.values()) {
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
