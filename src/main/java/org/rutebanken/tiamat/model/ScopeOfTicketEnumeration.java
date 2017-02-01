package org.rutebanken.tiamat.model;

public enum ScopeOfTicketEnumeration {

    UNKNOWN("unknown"),
    LOCAL_TICKET("localTicket"),
    NATIONAL_TICKET("nationalTicket"),
    INTERNATIONAL_TICKET("internationalTicket");
    private final String value;

    ScopeOfTicketEnumeration(String v) {
        value = v;
    }

    public static ScopeOfTicketEnumeration fromValue(String v) {
        for (ScopeOfTicketEnumeration c : ScopeOfTicketEnumeration.values()) {
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
