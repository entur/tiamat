package org.rutebanken.tiamat.model;

public enum MedicalFacilityEnumeration {

    UNKNOWN("unknown"),
    DEFIBRILLATOR("defibrillator"),
    ALCOHOL_TEST("alcoholTest");
    private final String value;

    MedicalFacilityEnumeration(String v) {
        value = v;
    }

    public static MedicalFacilityEnumeration fromValue(String v) {
        for (MedicalFacilityEnumeration c : MedicalFacilityEnumeration.values()) {
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
