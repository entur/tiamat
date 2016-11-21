package org.rutebanken.tiamat.model;

public enum LineSectionPointTypeEnumeration {

    NORMAL("normal"),
    INTERCHANGE("interchange"),
    MAJOR_INTERCHANGE("majorInterchange"),
    TERMINUS("terminus"),
    MAJOR_TERMINUS("majorTerminus"),
    OTHER("other");
    private final String value;

    LineSectionPointTypeEnumeration(String v) {
        value = v;
    }

    public static LineSectionPointTypeEnumeration fromValue(String v) {
        for (LineSectionPointTypeEnumeration c : LineSectionPointTypeEnumeration.values()) {
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
