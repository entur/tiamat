

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum ParkingStayEnumeration {

    SHORT_STAY("shortStay"),
    MID_TERM("midTerm"),
    LONG_TERM("longTerm"),
    DROPOFF("dropoff"),
    UNLIMITED("unlimited"),
    OTHER("other"),
    ALL("all");
    private final String value;

    ParkingStayEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ParkingStayEnumeration fromValue(String v) {
        for (ParkingStayEnumeration c: ParkingStayEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
