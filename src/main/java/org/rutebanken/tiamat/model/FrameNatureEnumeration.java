

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum FrameNatureEnumeration {

    PLANNED("planned"),
    OPERATIONAL("operational"),
    CONTINGENCY_PLAN("contingencyPlan"),
    OTHER("other");
    private final String value;

    FrameNatureEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FrameNatureEnumeration fromValue(String v) {
        for (FrameNatureEnumeration c: FrameNatureEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
