package org.rutebanken.tiamat.model;

public enum ZoneUseEnumeration {

    CANNOT_BOARD_AND_ALIGHT_IN_SAME_ZONE("cannotBoardAndAlightInSameZone"),
    MUST_ALIGHT_IN_ZONE("mustAlightInZone"),
    CANNOT_ALIGHT_IN_ZONE("cannotAlightInZone"),
    OTHER("other");
    private final String value;

    ZoneUseEnumeration(String v) {
        value = v;
    }

    public static ZoneUseEnumeration fromValue(String v) {
        for (ZoneUseEnumeration c : ZoneUseEnumeration.values()) {
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
