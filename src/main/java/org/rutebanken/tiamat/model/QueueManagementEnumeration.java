package org.rutebanken.tiamat.model;

public enum QueueManagementEnumeration {

    NONE("none"),
    MAZE("maze"),
    SEPARATE_LINES("separateLines"),
    TICKETED("ticketed"),
    OTHER("other");
    private final String value;

    QueueManagementEnumeration(String v) {
        value = v;
    }

    public static QueueManagementEnumeration fromValue(String v) {
        for (QueueManagementEnumeration c : QueueManagementEnumeration.values()) {
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
