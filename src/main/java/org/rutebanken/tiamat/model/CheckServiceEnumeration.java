

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum CheckServiceEnumeration {

    SELF_SERVICE("selfService"),
    COUNTER_SERVICE("counterService"),
    ANY_SERVICE("anyService"),
    OTHER("other");
    private final String value;

    CheckServiceEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CheckServiceEnumeration fromValue(String v) {
        for (CheckServiceEnumeration c: CheckServiceEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
