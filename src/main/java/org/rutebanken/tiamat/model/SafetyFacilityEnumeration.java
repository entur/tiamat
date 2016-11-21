

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum SafetyFacilityEnumeration {

    CC_TV("ccTv"),
    MOBILE_COVERAGE("mobileCoverage"),
    SOS_POINTS("sosPoints"),
    STAFFED("staffed");
    private final String value;

    SafetyFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SafetyFacilityEnumeration fromValue(String v) {
        for (SafetyFacilityEnumeration c: SafetyFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
