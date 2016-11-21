package org.rutebanken.tiamat.model;

public enum NameTypeEnumeration {

    ALIAS("alias"),
    TRANSLATION("translation"),
    COPY("copy"),
    LABEL("label"),
    OTHER("other");
    private final String value;

    NameTypeEnumeration(String v) {
        value = v;
    }

    public static NameTypeEnumeration fromValue(String v) {
        for (NameTypeEnumeration c : NameTypeEnumeration.values()) {
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
