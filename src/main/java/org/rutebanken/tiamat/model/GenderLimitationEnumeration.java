package org.rutebanken.tiamat.model;

public enum GenderLimitationEnumeration {

    BOTH("both"),
    FEMALE_ONLY("femaleOnly"),
    MALE_ONLY("maleOnly"),
    SAME_SEX_ONLY("sameSexOnly");
    private final String value;

    GenderLimitationEnumeration(String v) {
        value = v;
    }

    public static GenderLimitationEnumeration fromValue(String v) {
        for (GenderLimitationEnumeration c : GenderLimitationEnumeration.values()) {
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
