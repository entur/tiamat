package org.rutebanken.tiamat.model;

public enum PassageTypeEnumeration {

    NONE("none"),
    PATHWAY("pathway"),
    CORRIDOR("corridor"),
    OVERPASS("overpass"),
    UNDERPASS("underpass"),
    TUNNEL("tunnel"),
    OTHER("other");
    private final String value;

    PassageTypeEnumeration(String v) {
        value = v;
    }

    public static PassageTypeEnumeration fromValue(String v) {
        for (PassageTypeEnumeration c : PassageTypeEnumeration.values()) {
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
