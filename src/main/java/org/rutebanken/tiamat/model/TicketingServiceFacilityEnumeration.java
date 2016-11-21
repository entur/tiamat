

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum TicketingServiceFacilityEnumeration {

    PURCHASE("purchase"),
    COLLECTION("collection"),
    CARD_TOP_UP("cardTopUp"),
    RESERVATIONS("reservations"),
    EXCHANGE("exchange"),
    REFUND("refund"),
    RENEWAL("renewal"),
    EXCESS_FARES("excessFares"),
    OTHER("other"),
    ALL("all");
    private final String value;

    TicketingServiceFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TicketingServiceFacilityEnumeration fromValue(String v) {
        for (TicketingServiceFacilityEnumeration c: TicketingServiceFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
