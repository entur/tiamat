package org.rutebanken.tiamat.model;

public enum BookingProcessEnumeration {

    PRODUCT_NOT_AVAILABLE("productNotAvailable"),
    PRODUCT_NOT_BOOKABLE("productNotBookable"),
    BOOKABLE_THROUGH_INTERNATIONAL_SYSTEM("bookableThroughInternationalSystem"),
    BOOKABLE_THROUGH_NATIONAL_SYSTEM("bookableThroughNationalSystem"),
    BOOKABLE_MANUALLLY("bookableManuallly"),
    OTHER("other");
    private final String value;

    BookingProcessEnumeration(String v) {
        value = v;
    }

    public static BookingProcessEnumeration fromValue(String v) {
        for (BookingProcessEnumeration c : BookingProcessEnumeration.values()) {
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
