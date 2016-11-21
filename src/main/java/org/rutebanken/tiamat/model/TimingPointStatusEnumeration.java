

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum TimingPointStatusEnumeration {

    TIMING_POINT("timingPoint"),
    SECONDARY_TIMING_POINT("secondaryTimingPoint"),
    NOT_TIMING_POINT("notTimingPoint");
    private final String value;

    TimingPointStatusEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TimingPointStatusEnumeration fromValue(String v) {
        for (TimingPointStatusEnumeration c: TimingPointStatusEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
