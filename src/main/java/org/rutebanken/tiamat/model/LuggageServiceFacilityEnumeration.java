package org.rutebanken.tiamat.model;

public enum LuggageServiceFacilityEnumeration {

    OTHER("other"),
    LEFT_LUGGAGE("leftLuggage"),
    PORTERAGE("porterage"),
    FREE_TROLLEYS("freeTrolleys"),
    PAID_TROLLEYS("paidTrolleys"),
    COLLECT_AND_DELIVER_TO_STATION("collectAndDeliverToStation"),
    BAGGAGE_CHECK_IN_CHECK_OUT("baggageCheckInCheckOut");
    private final String value;

    LuggageServiceFacilityEnumeration(String v) {
        value = v;
    }

    public static LuggageServiceFacilityEnumeration fromValue(String v) {
        for (LuggageServiceFacilityEnumeration c : LuggageServiceFacilityEnumeration.values()) {
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
