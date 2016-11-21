package org.rutebanken.tiamat.model;

public enum EntranceTypeEnumeration {

    DOOR("door"),
    DOORWAY("doorway"),
    REVOLVING_DOOR("revolvingDoor"),
    SLIDING_DOORS("slidingDoors"),
    BARRIER("barrier"),
    TICKET_BARRIER("ticketBarrier"),
    ID_BARRIER("idBarrier"),
    GATE("gate"),
    STYLE("style"),
    OTHER("other");
    private final String value;

    EntranceTypeEnumeration(String v) {
        value = v;
    }

    public static EntranceTypeEnumeration fromValue(String v) {
        for (EntranceTypeEnumeration c : EntranceTypeEnumeration.values()) {
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
