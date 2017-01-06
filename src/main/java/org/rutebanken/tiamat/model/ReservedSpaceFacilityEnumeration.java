package org.rutebanken.tiamat.model;

public enum ReservedSpaceFacilityEnumeration {

    UNKNOWN("unknown"),
    LOUNGE("lounge"),
    HALL("hall"),
    MEETING_POINT("meetingPoint"),
    GROUP_POINT("groupPoint"),
    RECEPTION("reception"),
    SHELTER("shelter"),
    SEATS("seats");
    private final String value;

    ReservedSpaceFacilityEnumeration(String v) {
        value = v;
    }

    public static ReservedSpaceFacilityEnumeration fromValue(String v) {
        for (ReservedSpaceFacilityEnumeration c : ReservedSpaceFacilityEnumeration.values()) {
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
