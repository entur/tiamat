

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum TicketingFacilityEnumeration {

    UNKNOWN("unknown"),
    TICKET_MACHINES("ticketMachines"),
    TICKET_OFFICE("ticketOffice"),
    TICKET_ON_DEMAND_MACHINES("ticketOnDemandMachines"),
    MOBILE_TICKETING("mobileTicketing");
    private final String value;

    TicketingFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TicketingFacilityEnumeration fromValue(String v) {
        for (TicketingFacilityEnumeration c: TicketingFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
