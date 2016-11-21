package org.rutebanken.tiamat.model;

public enum LuggageServiceEnumeration {

    LEFT_LUGGAGE("leftLuggage"),
    PORTERAGE("porterage"),
    FREE_TROLLEYS("freeTrolleys"),
    PAID_TROLLEYS("paidTrolleys"),
    OTHER("other");
    private final String value;

    LuggageServiceEnumeration(String v) {
        value = v;
    }

    public static LuggageServiceEnumeration fromValue(String v) {
        for (LuggageServiceEnumeration c : LuggageServiceEnumeration.values()) {
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
