package org.rutebanken.tiamat.model;

public enum SanitaryFacilityEnumeration {

    NONE("none"),
    TOILET("toilet"),
    WHEEL_CHAIR_ACCESS_TOILET("wheelChairAccessToilet"),
    SHOWER("shower"),
    WASHING_AND_CHANGE_FACILITIES("washingAndChangeFacilities"),
    BABY_CHANGE("babyChange"),
    WHEELCHAIR_BABY_CHANGE("wheelchairBabyChange"),
    SHOE_SHINER("shoeShiner"),
    OTHER("other");
    private final String value;

    SanitaryFacilityEnumeration(String v) {
        value = v;
    }

    public static SanitaryFacilityEnumeration fromValue(String v) {
        for (SanitaryFacilityEnumeration c : SanitaryFacilityEnumeration.values()) {
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
