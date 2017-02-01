package org.rutebanken.tiamat.model;

public enum PassengerCommsFacilityEnumeration {

    UNKNOWN("unknown"),
    FREE_WIFI("freeWifi"),
    PUBLIC_WIFI("publicWifi"),
    POWER_SUPPLY_SOCKETS("powerSupplySockets"),

    TELEPHONE("telephone"),

    AUDIO_ENTERTAINMENT("audioEntertainment"),

    VIDEO_ENTERTAINMENT("videoEntertainment"),

    BUSINESS_SERVICES("businessServices"),
    INTERNET("internet"),
    POST_OFFICE("postOffice"),
    POST_BOX("postBox");
    private final String value;

    PassengerCommsFacilityEnumeration(String v) {
        value = v;
    }

    public static PassengerCommsFacilityEnumeration fromValue(String v) {
        for (PassengerCommsFacilityEnumeration c : PassengerCommsFacilityEnumeration.values()) {
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
