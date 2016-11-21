

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum StaffingEnumeration {

    FULL_TIME("fullTime"),
    PART_TIME("partTime"),
    UNMANNED("unmanned");
    private final String value;

    StaffingEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StaffingEnumeration fromValue(String v) {
        for (StaffingEnumeration c: StaffingEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
