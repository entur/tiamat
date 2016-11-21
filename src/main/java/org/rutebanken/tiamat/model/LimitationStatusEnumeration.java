

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum LimitationStatusEnumeration {


    TRUE("true"),

    FALSE("false"),

    UNKNOWN("unknown"),

    PARTIAL("partial");
    private final String value;

    LimitationStatusEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LimitationStatusEnumeration fromValue(String v) {
        for (LimitationStatusEnumeration c: LimitationStatusEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
