package org.rutebanken.tiamat.model;

public enum PassengerInformationEquipmentEnumeration {

    TIMETABLE_POSTER("timetablePoster"),
    FARE_INFORMATION("fareInformation"),
    LINE_NETWORK_PLAN("lineNetworkPlan"),
    LINE_TIMETABLE("lineTimetable"),
    STOP_TIMETABLE("stopTimetable"),
    JOURNEY_PLANNING("journeyPlanning"),
    INTERACTIVE_KIOSK("interactiveKiosk"),
    INFORMATION_DESK("informationDesk"),
    REAL_TIME_DEPARTURES("realTimeDepartures"),
    OTHER("other");
    private final String value;

    PassengerInformationEquipmentEnumeration(String v) {
        value = v;
    }

    public static PassengerInformationEquipmentEnumeration fromValue(String v) {
        for (PassengerInformationEquipmentEnumeration c : PassengerInformationEquipmentEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String value() {
        return value;
    }

}
