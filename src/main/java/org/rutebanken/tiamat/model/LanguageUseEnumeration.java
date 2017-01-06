package org.rutebanken.tiamat.model;

public enum LanguageUseEnumeration {

    NORMALLY_USED("normallyUsed"),
    UNDERSTOOD("understood"),
    NATIVE("native"),
    SPOKEN("spoken"),
    WRITTEN("written"),
    READ("read"),
    OTHER("other"),
    ALL_USES("allUses");
    private final String value;

    LanguageUseEnumeration(String v) {
        value = v;
    }

    public static LanguageUseEnumeration fromValue(String v) {
        for (LanguageUseEnumeration c : LanguageUseEnumeration.values()) {
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
