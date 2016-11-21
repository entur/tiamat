

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum GatedEnumeration {

    GATED_AREA("gatedArea"),
    OPEN_AREA("openArea"),
    UNKNOWN("unknown");
    private final String value;

    GatedEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GatedEnumeration fromValue(String v) {
        for (GatedEnumeration c: GatedEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
