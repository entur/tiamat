

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


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

    public String value() {
        return value;
    }

    public static AccessibilityToolEnumeration fromValue(String v) {
        for (AccessibilityToolEnumeration c: AccessibilityToolEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
