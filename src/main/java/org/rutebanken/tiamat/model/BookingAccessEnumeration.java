package org.rutebanken.tiamat.model;

public enum BookingAccessEnumeration {

    PUBLIC("public"),
    AUTHORISED_PUBLIC("authorisedPublic"),
    STAFF("staff"),
    OTHER("other");
    private final String value;

    BookingAccessEnumeration(String v) {
        value = v;
    }

    public static BookingAccessEnumeration fromValue(String v) {
        for (BookingAccessEnumeration c : BookingAccessEnumeration.values()) {
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
