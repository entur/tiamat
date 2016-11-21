

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum CateringFacilityEnumeration {


    BAR("bar"),

    BISTRO("bistro"),
    BUFFET("buffet"),

    NO_FOOD_AVAILABLE_AVAILABLE("noFoodAvailableAvailable"),

    NO_BEVERAGES_AVAILABLE("noBeveragesAvailable"),

    RESTAURANT("restaurant"),
    FIRST_CLASS_RESTAURANT("firstClassRestaurant"),

    TROLLEY("trolley"),
    COFFEE_SOHP("coffeeSohp"),
    HOT_FOOD_SERVICE("hotFoodService"),
    SELF_SERVICE("selfService"),

    SNACKS("snacks"),
    FOOD_VENDING_MACHINE("foodVendingMachine"),
    BEVERAGE_VENDING_MACHINE("beverageVendingMachine"),

    MINI_BAR("miniBar"),
    BREAKFAST_IN_CAR("breakfastInCar"),
    MEAL_AT_SEAT("mealAtSeat"),
    OTHER("other"),
    UNKNOWN("unknown");
    private final String value;

    CateringFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CateringFacilityEnumeration fromValue(String v) {
        for (CateringFacilityEnumeration c: CateringFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
