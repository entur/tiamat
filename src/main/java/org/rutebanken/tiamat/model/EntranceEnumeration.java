

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum EntranceEnumeration {

    OPENING("opening"),
    OPEN_DOOR("openDoor"),
    DOOR("door"),
    SWING_DOOR("swingDoor"),
    REVOLVING_DOOR("revolvingDoor"),
    AUTOMATIC_DOOR("automaticDoor"),
    TICKET_BARRIER("ticketBarrier"),
    GATE("gate"),
    OTHER("other");
    private final String value;

    EntranceEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EntranceEnumeration fromValue(String v) {
        for (EntranceEnumeration c: EntranceEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
