

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum SuitableEnumeration {

    SUITABLE("suitable"),
    NOT_SUITABLE("notSuitable");
    private final String value;

    SuitableEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SuitableEnumeration fromValue(String v) {
        for (SuitableEnumeration c: SuitableEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
