

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum CarServiceFacilityEnumeration {

    UNKNOWN("unknown"),
    CAR_WASH("carWash"),
    VALET_PARK("valetPark"),
    CAR_VALET_CLEAN("carValetClean"),
    OIL_CHANGE("oilChange"),
    ENGINE_WARMING("engineWarming"),
    PETROL("petrol");
    private final String value;

    CarServiceFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CarServiceFacilityEnumeration fromValue(String v) {
        for (CarServiceFacilityEnumeration c: CarServiceFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
