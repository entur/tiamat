package org.rutebanken.tiamat.model;

public enum PathDirectionEnumeration {

    ONE_WAY("oneWay"),
    TWO_WAY("twoWay");
    private final String value;

    PathDirectionEnumeration(String v) {
        value = v;
    }

    public static PathDirectionEnumeration fromValue(String v) {
        for (PathDirectionEnumeration c : PathDirectionEnumeration.values()) {
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
