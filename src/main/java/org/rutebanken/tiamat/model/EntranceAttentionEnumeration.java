package org.rutebanken.tiamat.model;

public enum EntranceAttentionEnumeration {

    NONE("none"),
    DOORBELL("doorbell"),
    HELP_POINT("helpPoint"),
    INTERCOM("intercom"),
    OTHER("other");
    private final String value;

    EntranceAttentionEnumeration(String v) {
        value = v;
    }

    public static EntranceAttentionEnumeration fromValue(String v) {
        for (EntranceAttentionEnumeration c : EntranceAttentionEnumeration.values()) {
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
