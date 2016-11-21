

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum AccommodationAccessEnumeration {

    OTHER("other"),
    FREE_SEATING("freeSeating"),

    RESERVATION("reservation"),
    STANDING("standing");
    private final String value;

    AccommodationAccessEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AccommodationAccessEnumeration fromValue(String v) {
        for (AccommodationAccessEnumeration c: AccommodationAccessEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
