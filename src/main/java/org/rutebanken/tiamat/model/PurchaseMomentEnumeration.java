package org.rutebanken.tiamat.model;

public enum PurchaseMomentEnumeration {

    ON_RESERVATION("onReservation"),
    BEFORE_BOARDING("beforeBoarding"),
    ON_BOARDING("onBoarding"),
    AFTER_BOARDING("afterBoarding"),
    ON_CHECK_OUT("onCheckOut"),
    OTHER("other");
    private final String value;

    PurchaseMomentEnumeration(String v) {
        value = v;
    }

    public static PurchaseMomentEnumeration fromValue(String v) {
        for (PurchaseMomentEnumeration c : PurchaseMomentEnumeration.values()) {
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
