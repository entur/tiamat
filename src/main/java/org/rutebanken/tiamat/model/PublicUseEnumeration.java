package org.rutebanken.tiamat.model;

public enum PublicUseEnumeration {

    ALL("all"),
    DISABLED_PUBLIC_ONLY("disabledPublicOnly"),
    AUTHORISED_PUBLIC_ONLY("authorisedPublicOnly"),
    STAFF_ONLY("staffOnly"),
    PUBLIC_ONLY("publicOnly");
    private final String value;

    PublicUseEnumeration(String v) {
        value = v;
    }

    public static PublicUseEnumeration fromValue(String v) {
        for (PublicUseEnumeration c : PublicUseEnumeration.values()) {
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
