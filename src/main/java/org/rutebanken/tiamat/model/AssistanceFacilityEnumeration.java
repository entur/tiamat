package org.rutebanken.tiamat.model;

public enum AssistanceFacilityEnumeration {

    PERSONAL_ASSISTANCE("personalAssistance"),
    BOARDING_ASSISTANCE("boardingAssistance"),
    WHEELCHAIR_ASSISTANCE("wheelchairAssistance"),
    UNACCOMPANIED_MINOR_ASSISTANCE("unaccompaniedMinorAssistance"),
    WHEELCHAIR_USE("wheelchairUse"),
    CONDUCTOR("conductor"),
    INFORMATION("information"),
    OTHER("other"),
    NONE("none"),
    ANY("any");

    private final String value;

    AssistanceFacilityEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static AssistanceFacilityEnumeration fromValue(String v) {
        for(AssistanceFacilityEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
