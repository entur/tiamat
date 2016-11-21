

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum DirectionTypeEnumeration {

    INBOUND("inbound"),
    OUTBOUND("outbound"),
    CLOCKWISE("clockwise"),
    ANTICLOCKWISE("anticlockwise");
    private final String value;

    DirectionTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DirectionTypeEnumeration fromValue(String v) {
        for (DirectionTypeEnumeration c: DirectionTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
