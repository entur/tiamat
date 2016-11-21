

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum BoardingPositionTypeEnumeration {

    UNKNOWN("unknown"),
    DOOR_FROM_AIRLINE_GATE("doorFromAirlineGate"),
    POSITION_ON_RAIL_PLATFORM("positionOnRailPlatform"),
    POSITION_ON_METRO_PLATFORM("positionOnMetroPlatform"),
    POSITION_AT_COACH_STOP("positionAtCoachStop"),
    POSITION_AT_BUS_STOP("positionAtBusStop"),
    BOAT_GANGWAY("boatGangway"),
    FERRY_GANGWAY("ferryGangway"),
    TELECABINEPLATFORM("telecabineplatform"),
    SET_DOWN_POINT("setDownPoint"),
    TAXI_BAY("taxiBay"),
    VEHICLE_LOADING_RAMP("vehicleLoadingRamp"),
    OTHER("other");
    private final String value;

    BoardingPositionTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BoardingPositionTypeEnumeration fromValue(String v) {
        for (BoardingPositionTypeEnumeration c: BoardingPositionTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
