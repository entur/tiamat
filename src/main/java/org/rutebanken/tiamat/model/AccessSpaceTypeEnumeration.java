package org.rutebanken.tiamat.model;

public enum AccessSpaceTypeEnumeration {

    CONCOURSE("concourse"),
    BOOKING_HALL("bookingHall"),
    FORECOURT("forecourt"),
    UNDERPASS("underpass"),
    OVERPASS("overpass"),
    PASSAGE("passage"),
    PASSAGE_SECTION("passageSection"),
    LIFT("lift"),
    GALLERY("gallery"),
    GARAGE("garage"),
    SHOP("shop"),
    WAITING_ROOM("waitingRoom"),
    RESTAURANT("restaurant"),
    OTHER("other"),
    STAIRCASE("staircase"),
    WC("wc");
    private final String value;

    AccessSpaceTypeEnumeration(String v) {
        value = v;
    }

    public static AccessSpaceTypeEnumeration fromValue(String v) {
        for (AccessSpaceTypeEnumeration c : AccessSpaceTypeEnumeration.values()) {
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
