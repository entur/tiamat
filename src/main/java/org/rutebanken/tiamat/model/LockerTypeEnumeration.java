

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum LockerTypeEnumeration {

    LEFT_LUGGAGE_OFFICE("leftLuggageOffice"),
    LOCKERS("lockers"),
    BIKE_RACK("bikeRack"),
    BIKE_CARRIAGE("bikeCarriage"),
    OTHER("other");
    private final String value;

    LockerTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LockerTypeEnumeration fromValue(String v) {
        for (LockerTypeEnumeration c: LockerTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
