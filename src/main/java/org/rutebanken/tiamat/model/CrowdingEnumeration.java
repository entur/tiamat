package org.rutebanken.tiamat.model;

public enum CrowdingEnumeration {

    VERY_QUIET("veryQuiet"),
    QUIET("quiet"),
    NORMAL("normal"),
    BUSY("busy"),
    VERY_BUSY("veryBusy");
    private final String value;

    CrowdingEnumeration(String v) {
        value = v;
    }

    public static CrowdingEnumeration fromValue(String v) {
        for (CrowdingEnumeration c : CrowdingEnumeration.values()) {
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
