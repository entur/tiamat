package org.rutebanken.tiamat.model;

public enum DisplayAssignmentTypeEnumeration {

    ARRIVALS("arrivals"),
    DEPARTURES("departures"),
    ALL("all");
    private final String value;

    DisplayAssignmentTypeEnumeration(String v) {
        value = v;
    }

    public static DisplayAssignmentTypeEnumeration fromValue(String v) {
        for (DisplayAssignmentTypeEnumeration c : DisplayAssignmentTypeEnumeration.values()) {
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
