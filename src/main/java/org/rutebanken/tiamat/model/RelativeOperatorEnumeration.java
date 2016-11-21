package org.rutebanken.tiamat.model;

public enum RelativeOperatorEnumeration {


    EQ,
    NE,
    GE,

    GT,
    LE,

    LT;

    public static RelativeOperatorEnumeration fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}
