package org.rutebanken.tiamat.model;

public enum AccommodationAccessEnumeration {

    OTHER("other"),
    FREE_SEATING("freeSeating"),

    RESERVATION("reservation"),
    STANDING("standing");
    private final String value;

    AccommodationAccessEnumeration(String v) {
        value = v;
    }

    public static AccommodationAccessEnumeration fromValue(String v) {
        for (AccommodationAccessEnumeration c : AccommodationAccessEnumeration.values()) {
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
