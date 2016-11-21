

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum LuggageServiceFacilityEnumeration {

    OTHER("other"),
    LEFT_LUGGAGE("leftLuggage"),
    PORTERAGE("porterage"),
    FREE_TROLLEYS("freeTrolleys"),
    PAID_TROLLEYS("paidTrolleys"),
    COLLECT_AND_DELIVER_TO_STATION("collectAndDeliverToStation"),
    BAGGAGE_CHECK_IN_CHECK_OUT("baggageCheckInCheckOut");
    private final String value;

    LuggageServiceFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LuggageServiceFacilityEnumeration fromValue(String v) {
        for (LuggageServiceFacilityEnumeration c: LuggageServiceFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
