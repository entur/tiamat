package org.rutebanken.tiamat.model;

public enum CouchetteFacilityEnumeration {

    UNKNOWN("unknown"),
    T_2("T2"),
    T_3("T3"),
    C_1("C1"),
    C_2("C2"),
    C_4("C4"),

    C_5("C5"),
    C_6("C6"),
    WHEELCHAIR("wheelchair"),
    OTHER("other");
    private final String value;

    CouchetteFacilityEnumeration(String v) {
        value = v;
    }

    public static CouchetteFacilityEnumeration fromValue(String v) {
        for (CouchetteFacilityEnumeration c : CouchetteFacilityEnumeration.values()) {
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
