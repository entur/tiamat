

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum RoadVehicleModeEnumeration {

    BUS("bus"),
    COACH("coach"),
    TROLLEY_BUS("trolleyBus"),
    TRAM("tram");
    private final String value;

    RoadVehicleModeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RoadVehicleModeEnumeration fromValue(String v) {
        for (RoadVehicleModeEnumeration c: RoadVehicleModeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
