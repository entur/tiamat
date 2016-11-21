

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum ModificationSetEnumeration {


    ALL("all"),

    CHANGES_ONLY("changesOnly");
    private final String value;

    ModificationSetEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ModificationSetEnumeration fromValue(String v) {
        for (ModificationSetEnumeration c: ModificationSetEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
