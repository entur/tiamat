

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum AllModesEnumeration {

    ALL("all"),
    UNKNOWN("unknown"),
    AIR("air"),
    BUS("bus"),
    TROLLEY_BUS("trolleyBus"),
    TRAM("tram"),
    COACH("coach"),
    RAIL("rail"),
    INTERCITY_RAIL("intercityRail"),
    URBAN_RAIL("urbanRail"),
    METRO("metro"),
    WATER("water"),
    CABLEWAY("cableway"),
    FUNICULAR("funicular"),
    TAXI("taxi"),

    SELF_DRIVE("selfDrive"),
    FOOT("foot"),
    BICYCLE("bicycle"),
    MOTORCYCLE("motorcycle"),
    CAR("car"),
    SHUTTLE("shuttle");
    private final String value;

    AllModesEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AllModesEnumeration fromValue(String v) {
        for (AllModesEnumeration c: AllModesEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
