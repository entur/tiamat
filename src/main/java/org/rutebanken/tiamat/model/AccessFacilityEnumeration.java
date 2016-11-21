

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum AccessFacilityEnumeration {

    UNKNOWN("unknown"),
    LIFT("lift"),
    ESCALATOR("escalator"),
    TRAVELATOR("travelator"),
    RAMP("ramp"),
    STAIRS("stairs"),
    SHUTTLE("shuttle"),
    NARROW_ENTRANCE("narrowEntrance"),
    BARRIER("barrier"),
    PALLET_ACCESS___LOW_FLOOR("palletAccess_lowFloor"),
    VALIDATOR("validator");
    private final String value;

    AccessFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AccessFacilityEnumeration fromValue(String v) {
        for (AccessFacilityEnumeration c: AccessFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
