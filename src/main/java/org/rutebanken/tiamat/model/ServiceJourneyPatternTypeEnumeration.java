package org.rutebanken.tiamat.model;

public enum ServiceJourneyPatternTypeEnumeration {

    PASSENGER("passenger"),

    GARAGE_RUN_OUT("garageRunOut"),

    GARAGE_RUN_IN("garageRunIn"),

    TURNING_MANOEUVRE("turningManoeuvre"),
    OTHER("other");
    private final String value;

    ServiceJourneyPatternTypeEnumeration(String v) {
        value = v;
    }

    public static ServiceJourneyPatternTypeEnumeration fromValue(String v) {
        for (ServiceJourneyPatternTypeEnumeration c : ServiceJourneyPatternTypeEnumeration.values()) {
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
