

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum AssistanceAvailabilityEnumeration {


    NONE("none"),

    AVAILABLE("available"),

    AVAILABLE_IF_BOOKED("availableIfBooked"),

    AVAILABLE_AT_CERTAIN_TIMES("availableAtCertainTimes"),

    UNKNOWN("unknown");
    private final String value;

    AssistanceAvailabilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AssistanceAvailabilityEnumeration fromValue(String v) {
        for (AssistanceAvailabilityEnumeration c: AssistanceAvailabilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
