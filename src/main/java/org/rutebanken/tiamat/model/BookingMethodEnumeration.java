

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum BookingMethodEnumeration {

    CALL_DRIVER("callDriver"),
    CALL_OFFICE("callOffice"),
    ONLINE("online"),
    OTHER("other"),
    PHONE_AT_STOP("phoneAtStop"),
    TEXT("text"),
    NONE("none");
    private final String value;

    BookingMethodEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BookingMethodEnumeration fromValue(String v) {
        for (BookingMethodEnumeration c: BookingMethodEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
