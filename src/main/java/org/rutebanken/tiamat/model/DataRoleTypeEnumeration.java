package org.rutebanken.tiamat.model;

public enum DataRoleTypeEnumeration {

    ALL("all"),
    COLLECTS("collects"),
    VALIDATES("validates"),
    AGGREGATES("aggregates"),
    DISTRIBUTES("distributes"),
    REDISTRIBUTES("redistributes"),
    CREATES("creates");
    private final String value;

    DataRoleTypeEnumeration(String v) {
        value = v;
    }

    public static DataRoleTypeEnumeration fromValue(String v) {
        for (DataRoleTypeEnumeration c : DataRoleTypeEnumeration.values()) {
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
