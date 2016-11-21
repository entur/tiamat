

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum StatusEnumeration {


    ACTIVE("active"),

    INACTIVE("inactive"),

    OTHER("other");
    private final String value;

    StatusEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatusEnumeration fromValue(String v) {
        for (StatusEnumeration c: StatusEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
