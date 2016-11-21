

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum ModificationEnumeration {


    NEW("new"),

    DELETE("delete"),

    REVISE("revise"),
    DELTA("delta");
    private final String value;

    ModificationEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ModificationEnumeration fromValue(String v) {
        for (ModificationEnumeration c: ModificationEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
