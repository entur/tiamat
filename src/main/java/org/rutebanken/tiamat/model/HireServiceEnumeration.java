

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum HireServiceEnumeration {

    CYCLE_HIRE("cycleHire"),
    MOTORCYCLE_HIRE("motorcycleHire"),
    CAR_HIRE("carHire"),
    RECREATIONAL_DEVICE_HIRE("recreationalDeviceHire");
    private final String value;

    HireServiceEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HireServiceEnumeration fromValue(String v) {
        for (HireServiceEnumeration c: HireServiceEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
