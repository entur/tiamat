package org.rutebanken.tiamat.model;

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

    public static TicketTypeEnumeration fromValue(String v) {
        for (TicketTypeEnumeration c : TicketTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String value() {
        return value;
    }

}
