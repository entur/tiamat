package org.rutebanken.tiamat.model;

public enum MandatoryEnumeration {


    REQUIRED("required"),

    OPTIONAL("optional"),

    NOT_ALLOWED("notAllowed");
    private final String value;

    MandatoryEnumeration(String v) {
        value = v;
    }

    public static MandatoryEnumeration fromValue(String v) {
        for (MandatoryEnumeration c : MandatoryEnumeration.values()) {
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
