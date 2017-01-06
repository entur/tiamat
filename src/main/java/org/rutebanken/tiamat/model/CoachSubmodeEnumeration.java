package org.rutebanken.tiamat.model;

public enum CoachSubmodeEnumeration {

    UNKNOWN("unknown"),
    UNDEFINED("undefined"),
    INTERNATIONAL_COACH("internationalCoach"),
    NATIONAL_COACH("nationalCoach"),
    SHUTTLE_COACH("shuttleCoach"),
    REGIONAL_COACH("regionalCoach"),
    SPECIAL_COACH("specialCoach"),
    SCHOOL_COACH("schoolCoach"),
    SIGHTSEEING_COACH("sightseeingCoach"),
    TOURIST_COACH("touristCoach"),
    COMMUTER_COACH("commuterCoach");
    private final String value;

    CoachSubmodeEnumeration(String v) {
        value = v;
    }

    public static CoachSubmodeEnumeration fromValue(String v) {
        for (CoachSubmodeEnumeration c : CoachSubmodeEnumeration.values()) {
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
