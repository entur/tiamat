

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum VersionTypeEnumeration {

    POINT("point"),
    BASELINE("baseline");
    private final String value;

    VersionTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VersionTypeEnumeration fromValue(String v) {
        for (VersionTypeEnumeration c: VersionTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
