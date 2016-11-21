

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum DayOfWeekEnumeration {

    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday"),
    EVERYDAY("Everyday"),
    WEEKDAYS("Weekdays"),
    WEEKEND("Weekend"),
    NONE("none");
    private final String value;

    DayOfWeekEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DayOfWeekEnumeration fromValue(String v) {
        for (DayOfWeekEnumeration c: DayOfWeekEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
