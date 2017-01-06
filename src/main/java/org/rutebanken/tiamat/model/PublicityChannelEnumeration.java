package org.rutebanken.tiamat.model;

public enum PublicityChannelEnumeration {


    ALL("all"),

    PRINTED_MEDIA("printedMedia"),

    DYNAMIC_MEDIA("dynamicMedia"),
    NONE("none");
    private final String value;

    PublicityChannelEnumeration(String v) {
        value = v;
    }

    public static PublicityChannelEnumeration fromValue(String v) {
        for (PublicityChannelEnumeration c : PublicityChannelEnumeration.values()) {
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
