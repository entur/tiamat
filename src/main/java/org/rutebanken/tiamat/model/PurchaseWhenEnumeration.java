

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


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

    public String value() {
        return value;
    }

    public static PurchaseWhenEnumeration fromValue(String v) {
        for (PurchaseWhenEnumeration c: PurchaseWhenEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
