package org.rutebanken.tiamat.model;

public enum OperatorActivitiesEnumeration {

    PASSENGER("passenger"),
    FREIGHT("freight"),
    INFRASTRUCTURE("infrastructure"),
    OTHER("other");
    private final String value;

    OperatorActivitiesEnumeration(String v) {
        value = v;
    }

    public static OperatorActivitiesEnumeration fromValue(String v) {
        for (OperatorActivitiesEnumeration c : OperatorActivitiesEnumeration.values()) {
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
