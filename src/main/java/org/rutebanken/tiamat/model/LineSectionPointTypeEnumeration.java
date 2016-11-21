

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum LineSectionPointTypeEnumeration {

    NORMAL("normal"),
    INTERCHANGE("interchange"),
    MAJOR_INTERCHANGE("majorInterchange"),
    TERMINUS("terminus"),
    MAJOR_TERMINUS("majorTerminus"),
    OTHER("other");
    private final String value;

    LineSectionPointTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LineSectionPointTypeEnumeration fromValue(String v) {
        for (LineSectionPointTypeEnumeration c: LineSectionPointTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
