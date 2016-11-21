

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum BookingProcessEnumeration {

    PRODUCT_NOT_AVAILABLE("productNotAvailable"),
    PRODUCT_NOT_BOOKABLE("productNotBookable"),
    BOOKABLE_THROUGH_INTERNATIONAL_SYSTEM("bookableThroughInternationalSystem"),
    BOOKABLE_THROUGH_NATIONAL_SYSTEM("bookableThroughNationalSystem"),
    BOOKABLE_MANUALLLY("bookableManuallly"),
    OTHER("other");
    private final String value;

    BookingProcessEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BookingProcessEnumeration fromValue(String v) {
        for (BookingProcessEnumeration c: BookingProcessEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
