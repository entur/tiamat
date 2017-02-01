package org.rutebanken.tiamat.model;

public enum AirSubmodeEnumeration {

    UNKNOWN("unknown"),
    UNDEFINED("undefined"),
    INTERNATIONAL_FLIGHT("internationalFlight"),
    DOMESTIC_FLIGHT("domesticFlight"),
    INTERCONTINENTAL_FLIGHT("intercontinentalFlight"),
    DOMESTIC_SCHEDULED_FLIGHT("domesticScheduledFlight"),
    SHUTTLE_FLIGHT("shuttleFlight"),
    INTERCONTINENTAL_CHARTER_FLIGHT("intercontinentalCharterFlight"),
    INTERNATIONAL_CHARTER_FLIGHT("internationalCharterFlight"),
    ROUND_TRIP_CHARTER_FLIGHT("roundTripCharterFlight"),
    SIGHTSEEING_FLIGHT("sightseeingFlight"),
    HELICOPTER_SERVICE("helicopterService"),
    DOMESTIC_CHARTER_FLIGHT("domesticCharterFlight"),
    SCHENGEN_AREA_FLIGHT("SchengenAreaFlight"),
    AIRSHIP_SERVICE("airshipService"),
    SHORT_HAUL_INTERNATIONAL_FLIGHT("shortHaulInternationalFlight"),
    CANAL_BARGE("canalBarge");
    private final String value;

    AirSubmodeEnumeration(String v) {
        value = v;
    }

    public static AirSubmodeEnumeration fromValue(String v) {
        for (AirSubmodeEnumeration c : AirSubmodeEnumeration.values()) {
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
