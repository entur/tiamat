package org.rutebanken.tiamat.model;

public enum PathHeadingEnumeration {

    LEFT("left"),
    RIGHT("right"),
    FORWARD("forward"),
    BACK("back");
    private final String value;

    PathHeadingEnumeration(String v) {
        value = v;
    }

    public static PathHeadingEnumeration fromValue(String v) {
        for (PathHeadingEnumeration c : PathHeadingEnumeration.values()) {
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
