

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum MetroSubmodeEnumeration {

    UNKNOWN("unknown"),
    UNDEFINED("undefined"),
    METRO("metro"),
    TUBE("tube"),
    URBAN_RAILWAY("urbanRailway");
    private final String value;

    MetroSubmodeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MetroSubmodeEnumeration fromValue(String v) {
        for (MetroSubmodeEnumeration c: MetroSubmodeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
