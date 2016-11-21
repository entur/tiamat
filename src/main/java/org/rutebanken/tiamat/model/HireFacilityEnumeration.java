

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum HireFacilityEnumeration {

    UNKNOWN("unknown"),
    CAR_HIRE("carHire"),
    MOTOR_CYCLE_HIRE("motorCycleHire"),
    CYCLE_HIRE("cycleHire"),
    TAXI("taxi"),
    RECREATION_DEVICE_HIRE("recreationDeviceHire");
    private final String value;

    HireFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HireFacilityEnumeration fromValue(String v) {
        for (HireFacilityEnumeration c: HireFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
