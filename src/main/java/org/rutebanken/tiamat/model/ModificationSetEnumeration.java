package org.rutebanken.tiamat.model;

public enum ModificationSetEnumeration {


    ALL("all"),

    CHANGES_ONLY("changesOnly");
    private final String value;

    ModificationSetEnumeration(String v) {
        value = v;
    }

    public static ModificationSetEnumeration fromValue(String v) {
        for (ModificationSetEnumeration c : ModificationSetEnumeration.values()) {
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
