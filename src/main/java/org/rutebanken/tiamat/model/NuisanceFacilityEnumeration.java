package org.rutebanken.tiamat.model;

public enum NuisanceFacilityEnumeration {

    UNKNOWN("unknown"),
    SMOKING("smoking"),
    NO_SMOKING("noSmoking"),
    MOBILE_PHONE_USE_ZONE("mobilePhoneUseZone"),
    MOBILE_PHONE_FREE_ZONE("mobilePhoneFreeZone");
    private final String value;

    NuisanceFacilityEnumeration(String v) {
        value = v;
    }

    public static NuisanceFacilityEnumeration fromValue(String v) {
        for (NuisanceFacilityEnumeration c : NuisanceFacilityEnumeration.values()) {
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
