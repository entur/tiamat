

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum AllVehicleModesOfTransportEnumeration {

    ALL("all"),
    UNKNOWN("unknown"),
    BUS("bus"),
    TROLLEY_BUS("trolleyBus"),
    TRAM("tram"),
    COACH("coach"),
    RAIL("rail"),
    INTERCITY_RAIL("intercityRail"),
    URBAN_RAIL("urbanRail"),
    METRO("metro"),
    AIR("air"),
    WATER("water"),
    CABLEWAY("cableway"),
    FUNICULAR("funicular"),
    TAXI("taxi"),

    SELF_DRIVE("selfDrive");
    private final String value;

    AllVehicleModesOfTransportEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AllVehicleModesOfTransportEnumeration fromValue(String v) {
        for (AllVehicleModesOfTransportEnumeration c: AllVehicleModesOfTransportEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
