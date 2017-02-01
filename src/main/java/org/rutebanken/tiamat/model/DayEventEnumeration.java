package org.rutebanken.tiamat.model;

public enum DayEventEnumeration {

    ANY_DAY("anyDay"),
    NORMAL_DAY("normalDay"),
    MARKET_DAY("marketDay"),
    MATCH_DAY("matchDay"),
    EVENT_DAY("eventDay");
    private final String value;

    DayEventEnumeration(String v) {
        value = v;
    }

    public static DayEventEnumeration fromValue(String v) {
        for (DayEventEnumeration c : DayEventEnumeration.values()) {
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
