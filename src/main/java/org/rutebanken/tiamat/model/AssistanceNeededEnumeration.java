

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum AssistanceNeededEnumeration {

    LEVEL_ACCESS("levelAccess"),
    STEP_NEGOTIATION("stepNegotiation"),
    RAMP_REQUIRED("rampRequired"),
    HOIST_REQUIRED("hoistRequired"),
    UNKNOWN("unknown");
    private final String value;

    AssistanceNeededEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AssistanceNeededEnumeration fromValue(String v) {
        for (AssistanceNeededEnumeration c: AssistanceNeededEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
