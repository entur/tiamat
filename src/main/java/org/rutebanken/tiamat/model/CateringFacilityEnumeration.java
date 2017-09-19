/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

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

    public static CateringFacilityEnumeration fromValue(String v) {
        for (CateringFacilityEnumeration c : CateringFacilityEnumeration.values()) {
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
