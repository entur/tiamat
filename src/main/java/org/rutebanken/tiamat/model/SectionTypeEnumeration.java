package org.rutebanken.tiamat.model;

public enum SectionTypeEnumeration {

    TRUNK("trunk"),
    BRANCH("branch"),
    END_LOOP("endLoop"),
    OTHER("other");
    private final String value;

    SectionTypeEnumeration(String v) {
        value = v;
    }

    public static SectionTypeEnumeration fromValue(String v) {
        for (SectionTypeEnumeration c : SectionTypeEnumeration.values()) {
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
