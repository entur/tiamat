

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum LuggageLockerFacilityEnumeration {

    OTHER("other"),

    LOCKERS("lockers"),
    OVERSIZE_LOCKERS("oversizeLockers"),
    LEFT_LUGGAGE_COUNTER("leftLuggageCounter"),
    BIKE_RACK("bikeRack"),
    CLOAKROOM("cloakroom");
    private final String value;

    LuggageLockerFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LuggageLockerFacilityEnumeration fromValue(String v) {
        for (LuggageLockerFacilityEnumeration c: LuggageLockerFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
