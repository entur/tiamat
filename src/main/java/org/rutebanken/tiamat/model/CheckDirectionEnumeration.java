

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum CheckDirectionEnumeration {

    FORWARDS("forwards"),
    BACKWARDS("backwards"),
    BOTH_WAYS("bothWays");
    private final String value;

    CheckDirectionEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CheckDirectionEnumeration fromValue(String v) {
        for (CheckDirectionEnumeration c: CheckDirectionEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
