package org.rutebanken.tiamat.model;

public enum CommunicationServiceEnumeration {

    FREE_WIFI("freeWifi"),
    PUBLIC_WIFI("publicWifi"),
    PHONE("phone"),
    INTERNET("internet"),
    MOBILE_COVERAGE("mobileCoverage"),
    VIDEO_ENTERTAINMENT("videoEntertainment"),
    AUDIO_ENTERTAINMENT("audioEntertainment"),
    POSTBOX("postbox"),
    POST_OFFICE("postOffice"),
    BUSINESS_SERVICES("businessServices");
    private final String value;

    CommunicationServiceEnumeration(String v) {
        value = v;
    }

    public static CommunicationServiceEnumeration fromValue(String v) {
        for (CommunicationServiceEnumeration c : CommunicationServiceEnumeration.values()) {
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
