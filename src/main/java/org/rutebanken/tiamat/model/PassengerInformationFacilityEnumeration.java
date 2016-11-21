

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum PassengerInformationFacilityEnumeration {

    NEXT_STOP_INDICATOR("nextStopIndicator"),
    STOP_ANNOUNCEMENTS("stopAnnouncements"),
    PASSENGER_INFORMATION_DISPLAY("passengerInformationDisplay"),
    REAL_TIME_CONNECTIONS("realTimeConnections"),
    OTHER("other");
    private final String value;

    PassengerInformationFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PassengerInformationFacilityEnumeration fromValue(String v) {
        for (PassengerInformationFacilityEnumeration c: PassengerInformationFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
