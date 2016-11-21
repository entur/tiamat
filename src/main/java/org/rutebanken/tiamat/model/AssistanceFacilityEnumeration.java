

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum AssistanceFacilityEnumeration {

    PERSONAL_ASSISTANCE("personalAssistance"),
    BOARDING_ASSISTANCE("boardingAssistance"),
    WHEECHAIR_ASSISTANCE("wheechairAssistance"),
    UNACCOMPANIED_MINOR_ASSISTANCE("unaccompaniedMinorAssistance"),
    WHEELCHAIR_USE("wheelchairUse"),
    CONDUCTOR("conductor"),
    INFORMATION("information"),
    OTHER("other"),
    NONE("none"),
    ANY("any");
    private final String value;

    AssistanceFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AssistanceFacilityEnumeration fromValue(String v) {
        for (AssistanceFacilityEnumeration c: AssistanceFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
