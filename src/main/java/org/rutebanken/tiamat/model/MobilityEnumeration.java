package org.rutebanken.tiamat.model;

public enum MobilityEnumeration {

    WHEELCHAIR("wheelchair"),
    ASSISTED_WHEELCHAIR("assistedWheelchair"),
    MOTORIZED_WHEELCHAIR("motorizedWheelchair"),
    MOBILITY_SCOOTER("mobilityScooter"),
    ROAD_MOBILITY_SCOOTER("roadMobilityScooter"),
    WALKING_FRAME("walkingFrame"),
    RESTRICTED_MOBILITY("restrictedMobility"),
    OTHER_MOBILITY_NEED("otherMobilityNeed"),
    NORMAL("normal");
    private final String value;

    MobilityEnumeration(String v) {
        value = v;
    }

    public static MobilityEnumeration fromValue(String v) {
        for (MobilityEnumeration c : MobilityEnumeration.values()) {
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
