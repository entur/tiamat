

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum MedicalNeedEnumeration {

    ALLERGIC("allergic"),
    HEART_CONDITION("heartCondition"),
    OTHER_MEDICAL_NEED("otherMedicalNeed");
    private final String value;

    MedicalNeedEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MedicalNeedEnumeration fromValue(String v) {
        for (MedicalNeedEnumeration c: MedicalNeedEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
