

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum GradientEnumeration {

    VERY_STEEP("verySteep"),
    STEEP("steep"),
    MEDIUM("medium"),
    GENTLE("gentle"),
    LEVEL("level");
    private final String value;

    GradientEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GradientEnumeration fromValue(String v) {
        for (GradientEnumeration c: GradientEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
