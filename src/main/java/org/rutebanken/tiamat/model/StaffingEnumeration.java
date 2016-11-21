package org.rutebanken.tiamat.model;

public enum StaffingEnumeration {

    FULL_TIME("fullTime"),
    PART_TIME("partTime"),
    UNMANNED("unmanned");
    private final String value;

    StaffingEnumeration(String v) {
        value = v;
    }

    public static StaffingEnumeration fromValue(String v) {
        for (StaffingEnumeration c : StaffingEnumeration.values()) {
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
