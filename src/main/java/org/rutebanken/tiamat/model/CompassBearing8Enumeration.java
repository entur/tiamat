package org.rutebanken.tiamat.model;

public enum CompassBearing8Enumeration {

    SW,
    SE,
    NW,
    NE,
    W,
    E,
    S,
    N;

    public static CompassBearing8Enumeration fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}
