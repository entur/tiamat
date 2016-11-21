

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum MedicalFacilityEnumeration {

    UNKNOWN("unknown"),
    DEFIBRILLATOR("defibrillator"),
    ALCOHOL_TEST("alcoholTest");
    private final String value;

    MedicalFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MedicalFacilityEnumeration fromValue(String v) {
        for (MedicalFacilityEnumeration c: MedicalFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
