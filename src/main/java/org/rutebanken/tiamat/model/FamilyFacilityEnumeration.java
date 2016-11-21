

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum FamilyFacilityEnumeration {

    NONE("none"),
    SERVICES_FOR_CHILDREN("servicesForChildren"),
    SERVICES_FOR_ARMY_FAMILIES("servicesForArmyFamilies"),
    NURSERY_SERVICE("nurseryService");
    private final String value;

    FamilyFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FamilyFacilityEnumeration fromValue(String v) {
        for (FamilyFacilityEnumeration c: FamilyFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
