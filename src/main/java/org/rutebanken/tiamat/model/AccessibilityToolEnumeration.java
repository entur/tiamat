package org.rutebanken.tiamat.model;

public enum AccessibilityToolEnumeration {

    WHEELCHAIR("wheelchair"),
    WALKINGSTICK("walkingstick"),
    AUDIO_NAVIGATOR("audioNavigator"),
    VISUAL_NAVIGATOR("visualNavigator"),
    PASSENGER_CART("passengerCart"),
    PUSHCHAIR("pushchair"),
    UMBRELLA("umbrella"),
    BUGGY("buggy"),
    OTHER("other");
    private final String value;

    AccessibilityToolEnumeration(String v) {
        value = v;
    }

    public static AccessibilityToolEnumeration fromValue(String v) {
        for (AccessibilityToolEnumeration c : AccessibilityToolEnumeration.values()) {
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
