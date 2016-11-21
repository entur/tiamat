

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


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

    public String value() {
        return value;
    }

    public static EmergencyServiceEnumeration fromValue(String v) {
        for (EmergencyServiceEnumeration c: EmergencyServiceEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
