package org.rutebanken.tiamat.model;

public enum ClassRefTypeEnumeration {


    MEMBERS("members"),

    MEMBER_REFERENCES("memberReferences"),

    ALL("all");
    private final String value;

    ClassRefTypeEnumeration(String v) {
        value = v;
    }

    public static ClassRefTypeEnumeration fromValue(String v) {
        for (ClassRefTypeEnumeration c : ClassRefTypeEnumeration.values()) {
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
