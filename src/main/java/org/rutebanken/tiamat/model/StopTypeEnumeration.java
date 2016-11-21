

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum StopTypeEnumeration {

    ONSTREET_BUS("onstreetBus"),
    ONSTREET_TRAM("onstreetTram"),
    AIRPORT("airport"),
    RAIL_STATION("railStation"),
    METRO_STATION("metroStation"),
    BUS_STATION("busStation"),
    COACH_STATION("coachStation"),
    TRAM_STATION("tramStation"),
    HARBOUR_PORT("harbourPort"),
    FERRY_PORT("ferryPort"),
    FERRY_STOP("ferryStop"),
    LIFT_STATION("liftStation"),
    VEHICLE_RAIL_INTERCHANGE("vehicleRailInterchange"),
    OTHER("other");
    private final String value;

    StopTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StopTypeEnumeration fromValue(String v) {
        for (StopTypeEnumeration c: StopTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
