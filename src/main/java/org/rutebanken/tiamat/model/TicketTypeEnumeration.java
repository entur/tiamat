

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum TicketTypeEnumeration {

    STANDARD("standard"),
    PROMOTION("promotion"),
    CONCESSION("concession"),
    GROUP("group"),
    SEASON("season"),
    CARNET("carnet"),
    TRAVEL_CARD("travelCard"),
    OTHER("other"),
    ALL("all");
    private final String value;

    TicketTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TicketTypeEnumeration fromValue(String v) {
        for (TicketTypeEnumeration c: TicketTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
