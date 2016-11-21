

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum DirectionOfUseEnumeration {

    UP("up"),
    DOWN("down"),
    BOTH("both");
    private final String value;

    DirectionOfUseEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DirectionOfUseEnumeration fromValue(String v) {
        for (DirectionOfUseEnumeration c: DirectionOfUseEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
