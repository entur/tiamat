

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum LuggageServiceEnumeration {

    LEFT_LUGGAGE("leftLuggage"),
    PORTERAGE("porterage"),
    FREE_TROLLEYS("freeTrolleys"),
    PAID_TROLLEYS("paidTrolleys"),
    OTHER("other");
    private final String value;

    LuggageServiceEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LuggageServiceEnumeration fromValue(String v) {
        for (LuggageServiceEnumeration c: LuggageServiceEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
