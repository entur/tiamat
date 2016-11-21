package org.rutebanken.tiamat.model;

public enum TicketValidatorEnumeration {

    PAPER_STAMP("paperStamp"),
    CONTACT_LESS("contactLess"),
    MAGNETIC("magnetic"),
    OTHER("other");
    private final String value;

    TicketValidatorEnumeration(String v) {
        value = v;
    }

    public static TicketValidatorEnumeration fromValue(String v) {
        for (TicketValidatorEnumeration c : TicketValidatorEnumeration.values()) {
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
