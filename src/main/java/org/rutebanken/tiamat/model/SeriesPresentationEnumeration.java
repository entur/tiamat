package org.rutebanken.tiamat.model;

public enum SeriesPresentationEnumeration {

    NONE("none"),
    REQUIRED("required"),
    OPTIONAL_LEFT("optionalLeft"),
    OPTIONAL_RIGHT("optionalRight");
    private final String value;

    SeriesPresentationEnumeration(String v) {
        value = v;
    }

    public static SeriesPresentationEnumeration fromValue(String v) {
        for (SeriesPresentationEnumeration c : SeriesPresentationEnumeration.values()) {
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
