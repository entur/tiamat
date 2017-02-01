package org.rutebanken.tiamat.model;

public enum EmergencyServiceEnumeration {

    POLICE("police"),
    FIRE("fire"),
    FIRST_AID("firstAid"),
    SOS_POINT("sosPoint"),
    OTHER("other");
    private final String value;

    EmergencyServiceEnumeration(String v) {
        value = v;
    }

    public static EmergencyServiceEnumeration fromValue(String v) {
        for (EmergencyServiceEnumeration c : EmergencyServiceEnumeration.values()) {
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
