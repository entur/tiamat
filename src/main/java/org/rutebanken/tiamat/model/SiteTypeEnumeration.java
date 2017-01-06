package org.rutebanken.tiamat.model;

public enum SiteTypeEnumeration {

    SCHOOL("school"),
    UNIVERSITY("university"),
    WORKS("works"),
    OFFICE("office"),
    MILITARY_BASE("militaryBase"),
    RETAIL("retail"),
    OTHER("other");
    private final String value;

    SiteTypeEnumeration(String v) {
        value = v;
    }

    public static SiteTypeEnumeration fromValue(String v) {
        for (SiteTypeEnumeration c : SiteTypeEnumeration.values()) {
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
