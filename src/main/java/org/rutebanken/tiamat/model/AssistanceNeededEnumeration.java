package org.rutebanken.tiamat.model;

public enum AssistanceNeededEnumeration {

    LEVEL_ACCESS("levelAccess"),
    STEP_NEGOTIATION("stepNegotiation"),
    RAMP_REQUIRED("rampRequired"),
    HOIST_REQUIRED("hoistRequired"),
    UNKNOWN("unknown");
    private final String value;

    AssistanceNeededEnumeration(String v) {
        value = v;
    }

    public static AssistanceNeededEnumeration fromValue(String v) {
        for (AssistanceNeededEnumeration c : AssistanceNeededEnumeration.values()) {
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
