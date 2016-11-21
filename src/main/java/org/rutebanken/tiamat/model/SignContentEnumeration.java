

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum SignContentEnumeration {

    ENTRANCE("entrance"),
    EXIT("exit"),
    EMERGENCY_EXIT("emergencyExit"),
    TRANSPORT_MODE("transportMode"),
    NO_SMOKING("noSmoking"),
    TICKETS("tickets"),
    ASSISTANCE("assistance"),
    SOS_PHONE("sosPhone"),
    TOUCH_POINT("touchPoint"),
    MEETING_POINT("meetingPoint"),
    TRANSPORT_MODE_POINT("TransportModePoint"),
    OTHER("other");
    private final String value;

    SignContentEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SignContentEnumeration fromValue(String v) {
        for (SignContentEnumeration c: SignContentEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
