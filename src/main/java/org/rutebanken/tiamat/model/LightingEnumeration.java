

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum LightingEnumeration {

    WELL_LIT("wellLit"),
    POORLY_LIT("poorlyLit"),
    UNLIT("unlit"),
    UNKNOWN("unknown"),
    OTHER("other");
    private final String value;

    LightingEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LightingEnumeration fromValue(String v) {
        for (LightingEnumeration c: LightingEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
