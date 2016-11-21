

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum VehicleOrientationEnumeration {

    FORWARDS("forwards"),
    BACKWARDS("backwards"),
    UNKNOWN("unknown");
    private final String value;

    VehicleOrientationEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VehicleOrientationEnumeration fromValue(String v) {
        for (VehicleOrientationEnumeration c: VehicleOrientationEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
