

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum TicketValidatorEnumeration {

    PAPER_STAMP("paperStamp"),
    CONTACT_LESS("contactLess"),
    MAGNETIC("magnetic"),
    OTHER("other");
    private final String value;

    TicketValidatorEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TicketValidatorEnumeration fromValue(String v) {
        for (TicketValidatorEnumeration c: TicketValidatorEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
