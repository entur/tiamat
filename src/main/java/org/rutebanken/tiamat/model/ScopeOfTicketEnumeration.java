

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum ScopeOfTicketEnumeration {

    UNKNOWN("unknown"),
    LOCAL_TICKET("localTicket"),
    NATIONAL_TICKET("nationalTicket"),
    INTERNATIONAL_TICKET("internationalTicket");
    private final String value;

    ScopeOfTicketEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ScopeOfTicketEnumeration fromValue(String v) {
        for (ScopeOfTicketEnumeration c: ScopeOfTicketEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
