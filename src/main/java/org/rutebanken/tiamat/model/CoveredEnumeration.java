

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum CoveredEnumeration {

    INDOORS("indoors"),
    OUTDOORS("outdoors"),
    COVERED("covered"),
    MIXED("mixed"),
    UNKNOWN("unknown");
    private final String value;

    CoveredEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CoveredEnumeration fromValue(String v) {
        for (CoveredEnumeration c: CoveredEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
