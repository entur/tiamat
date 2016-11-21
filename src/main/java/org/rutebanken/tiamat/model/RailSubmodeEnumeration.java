

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum RailSubmodeEnumeration {

    UNKNOWN("unknown"),
    LOCAL("local"),

    HIGH_SPEED_RAIL("highSpeedRail"),

    SUBURBAN_RAILWAY("suburbanRailway"),

    REGIONAL_RAIL("regionalRail"),

    INTERREGIONAL_RAIL("interregionalRail"),

    LONG_DISTANCE("longDistance"),
    INTERMATIONAL("intermational"),
    SLEEPER_RAIL_SERVICE("sleeperRailService"),
    NIGHT_RAIL("nightRail"),

    CAR_TRANSPORT_RAIL_SERVICE("carTransportRailService"),

    TOURIST_RAILWAY("touristRailway"),
    RAIL_SHUTTLE("railShuttle"),
    REPLACEMENT_RAIL_SERVICE("replacementRailService"),
    SPECIAL_TRAIN("specialTrain"),
    CROSS_COUNTRY_RAIL("crossCountryRail"),

    RACK_AND_PINION_RAILWAY("rackAndPinionRailway");
    private final String value;

    RailSubmodeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RailSubmodeEnumeration fromValue(String v) {
        for (RailSubmodeEnumeration c: RailSubmodeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
