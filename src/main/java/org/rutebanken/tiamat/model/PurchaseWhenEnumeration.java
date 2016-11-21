package org.rutebanken.tiamat.model;

public enum PurchaseWhenEnumeration {

    TIME_OF_TRAVEL_ONLY("timeOfTravelOnly"),
    DAY_OF_TRAVEL_ONLY("dayOfTravelOnly"),
    UNTIL_PREVIOUS_DAY("untilPreviousDay"),
    ADVANCE_ONLY("advanceOnly"),
    ADVANCE_AND_DAY_OF_TRAVEL("advanceAndDayOfTravel"),
    OTHER("other");
    private final String value;

    PurchaseWhenEnumeration(String v) {
        value = v;
    }

    public static PurchaseWhenEnumeration fromValue(String v) {
        for (PurchaseWhenEnumeration c : PurchaseWhenEnumeration.values()) {
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
