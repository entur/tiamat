

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum BerthFacilityEnumeration {

    UPPER("upper"),
    LOWER("lower"),
    BOTH("both");
    private final String value;

    BerthFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BerthFacilityEnumeration fromValue(String v) {
        for (BerthFacilityEnumeration c: BerthFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
