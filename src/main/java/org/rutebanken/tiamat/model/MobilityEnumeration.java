

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum MobilityEnumeration {

    WHEELCHAIR("wheelchair"),
    ASSISTED_WHEELCHAIR("assistedWheelchair"),
    MOTORIZED_WHEELCHAIR("motorizedWheelchair"),
    MOBILITY_SCOOTER("mobilityScooter"),
    ROAD_MOBILITY_SCOOTER("roadMobilityScooter"),
    WALKING_FRAME("walkingFrame"),
    RESTRICTED_MOBILITY("restrictedMobility"),
    OTHER_MOBILITY_NEED("otherMobilityNeed"),
    NORMAL("normal");
    private final String value;

    MobilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MobilityEnumeration fromValue(String v) {
        for (MobilityEnumeration c: MobilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
