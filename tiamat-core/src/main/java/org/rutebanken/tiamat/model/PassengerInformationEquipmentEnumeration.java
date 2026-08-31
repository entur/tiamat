package org.rutebanken.tiamat.model;

public enum PassengerInformationEquipmentEnumeration {
    FARE_INFORMATION("fareInformation"),
    LINE_NETWORK_PLAN("lineNetworkPlan"),
    LINE_TIMETABLE("lineTimetable"),
    INFORMATION_DESK("informationDesk"),
    REAL_TIME_DEPARTURES("realTimeDepartures"),
    OTHER("other");
    private final String value;

    private PassengerInformationEquipmentEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static PassengerInformationEquipmentEnumeration fromValue(String v) {
        for(PassengerInformationEquipmentEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
