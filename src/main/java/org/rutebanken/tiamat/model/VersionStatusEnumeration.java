

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum VersionStatusEnumeration {

    DRAFT("draft"),
    VERSIONED("versioned"),
    DEPRECATED("deprecated"),
    OTHER("other");
    private final String value;

    VersionStatusEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VersionStatusEnumeration fromValue(String v) {
        for (VersionStatusEnumeration c: VersionStatusEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
