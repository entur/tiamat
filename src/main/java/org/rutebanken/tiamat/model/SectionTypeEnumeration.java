

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum SectionTypeEnumeration {

    TRUNK("trunk"),
    BRANCH("branch"),
    END_LOOP("endLoop"),
    OTHER("other");
    private final String value;

    SectionTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SectionTypeEnumeration fromValue(String v) {
        for (SectionTypeEnumeration c: SectionTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
