

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum TypeOfInfolinkEnumeration {

    CONTACT("contact"),
    RESOURCE("resource"),
    INFO("info"),
    OTHER("other");
    private final String value;

    TypeOfInfolinkEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypeOfInfolinkEnumeration fromValue(String v) {
        for (TypeOfInfolinkEnumeration c: TypeOfInfolinkEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
