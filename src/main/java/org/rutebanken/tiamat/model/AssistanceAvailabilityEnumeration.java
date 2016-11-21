package org.rutebanken.tiamat.model;

public enum AssistanceAvailabilityEnumeration {


    NONE("none"),

    AVAILABLE("available"),

    AVAILABLE_IF_BOOKED("availableIfBooked"),

    AVAILABLE_AT_CERTAIN_TIMES("availableAtCertainTimes"),

    UNKNOWN("unknown");
    private final String value;

    AssistanceAvailabilityEnumeration(String v) {
        value = v;
    }

    public static AssistanceAvailabilityEnumeration fromValue(String v) {
        for (AssistanceAvailabilityEnumeration c : AssistanceAvailabilityEnumeration.values()) {
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
