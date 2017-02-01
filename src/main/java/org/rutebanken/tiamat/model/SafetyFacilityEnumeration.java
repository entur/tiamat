package org.rutebanken.tiamat.model;

public enum SafetyFacilityEnumeration {

    CC_TV("ccTv"),
    MOBILE_COVERAGE("mobileCoverage"),
    SOS_POINTS("sosPoints"),
    STAFFED("staffed");
    private final String value;

    SafetyFacilityEnumeration(String v) {
        value = v;
    }

    public static SafetyFacilityEnumeration fromValue(String v) {
        for (SafetyFacilityEnumeration c : SafetyFacilityEnumeration.values()) {
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
