package org.rutebanken.tiamat.model;

public enum AssistedBoardingLocationEnumeration {

    BOARD_AT_ANY_DOOR("boardAtAnyDoor"),
    BOARD_ONLY_AT_SPECIFIED_POSITIONS("boardOnlyAtSpecifiedPositions"),
    UNKNOWN("unknown");
    private final String value;

    AssistedBoardingLocationEnumeration(String v) {
        value = v;
    }

    public static AssistedBoardingLocationEnumeration fromValue(String v) {
        for (AssistedBoardingLocationEnumeration c : AssistedBoardingLocationEnumeration.values()) {
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
