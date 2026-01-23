package org.rutebanken.tiamat.model;

public enum PassengerInformationFacilityEnumeration {
    NEXT_STOP_INDICATOR("nextStopIndicator"),
    STOP_ANNOUNCEMENTS("stopAnnouncements"),
    PASSENGER_INFORMATION_DISPLAY("passengerInformationDisplay"),
    REAL_TIME_CONNECTIONS("realTimeConnections"),
    OTHER("other");
    private final String value;

    private PassengerInformationFacilityEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static PassengerInformationFacilityEnumeration fromValue(String v) {
        for(PassengerInformationFacilityEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
