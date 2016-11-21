package org.rutebanken.tiamat.model;

public enum TypeOfInfolinkEnumeration {

    CONTACT("contact"),
    RESOURCE("resource"),
    INFO("info"),
    OTHER("other");
    private final String value;

    TypeOfInfolinkEnumeration(String v) {
        value = v;
    }

    public static TypeOfInfolinkEnumeration fromValue(String v) {
        for (TypeOfInfolinkEnumeration c : TypeOfInfolinkEnumeration.values()) {
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
