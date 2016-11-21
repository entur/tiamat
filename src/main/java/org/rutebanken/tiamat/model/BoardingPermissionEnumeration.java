package org.rutebanken.tiamat.model;

public enum BoardingPermissionEnumeration {

    NORMAL("normal"),
    EARLY_BOARDING_POSSIBLE_BEFORE_DEPARTURE("earlyBoardingPossibleBeforeDeparture"),
    LATE_ALIGHTING_POSSIBLE_AFTER_ARRIVAL("lateAlightingPossibleAfterArrival"),
    OVERNIGHT_STAY_ONBOARD_ALLOWED("overnightStayOnboardAllowed");
    private final String value;

    BoardingPermissionEnumeration(String v) {
        value = v;
    }

    public static BoardingPermissionEnumeration fromValue(String v) {
        for (BoardingPermissionEnumeration c : BoardingPermissionEnumeration.values()) {
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
