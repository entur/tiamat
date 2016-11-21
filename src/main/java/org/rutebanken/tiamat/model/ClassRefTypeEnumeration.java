

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum ClassRefTypeEnumeration {


    MEMBERS("members"),

    MEMBER_REFERENCES("memberReferences"),

    ALL("all");
    private final String value;

    ClassRefTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClassRefTypeEnumeration fromValue(String v) {
        for (ClassRefTypeEnumeration c: ClassRefTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
