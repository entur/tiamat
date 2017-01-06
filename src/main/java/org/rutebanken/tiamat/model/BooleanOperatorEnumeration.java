package org.rutebanken.tiamat.model;

public enum BooleanOperatorEnumeration {


    AND,
    OR,
    NOT,
    XOR;

    public static BooleanOperatorEnumeration fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}
