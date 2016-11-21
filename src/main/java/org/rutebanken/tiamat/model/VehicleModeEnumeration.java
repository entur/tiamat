

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum VehicleModeEnumeration {

    AIR("air"),
    BUS("bus"),
    COACH("coach"),
    FERRY("ferry"),
    METRO("metro"),
    RAIL("rail"),
    TROLLEY_BUS("trolleyBus"),
    TRAM("tram"),
    WATER("water"),
    CABLEWAY("cableway"),
    FUNICULAR("funicular"),
    LIFT("lift"),
    OTHER("other");
    private final String value;

    VehicleModeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VehicleModeEnumeration fromValue(String v) {
        for (VehicleModeEnumeration c: VehicleModeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
