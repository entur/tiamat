package org.rutebanken.tiamat.model;

public enum MobilityFacilityEnumeration {


    UNKNOWN("unknown"),

    LOW_FLOOR("lowFloor"),

    STEP_FREE_ACCESS("stepFreeAccess"),

    SUITABLE_FOR_WHEELCHAIRS("suitableForWheelchairs"),
    SUITABLE_FOR_HEAVILIY_DISABLED("suitableForHeaviliyDisabled"),

    BOARDING_ASSISTANCE("boardingAssistance"),
    ONBOARD_ASSISTANCE("onboardAssistance"),
    UNACCOMPANIED_MINOR_ASSISTANCE("unaccompaniedMinorAssistance"),
    TACTILE_PATFORM_EDGES("tactilePatformEdges"),
    TACTILE_GUIDING_STRIPS("tactileGuidingStrips");
    private final String value;

    MobilityFacilityEnumeration(String v) {
        value = v;
    }

    public static MobilityFacilityEnumeration fromValue(String v) {
        for (MobilityFacilityEnumeration c : MobilityFacilityEnumeration.values()) {
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
