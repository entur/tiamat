package org.rutebanken.tiamat.model;

public enum AssistanceAvailabilityEnumeration {

    NONE("none"),
    AVAILABLE("available"),
    AVAILABLE_IF_BOOKED("availableIfBooked"),
    AVAILABLE_AT_CERTAIN_TIMES("availableAtCertainTimes"),
    UNKNOWN("unknown");

    private final String value;

    AssistanceAvailabilityEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static AssistanceAvailabilityEnumeration fromValue(String v) {
        for(AssistanceAvailabilityEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
