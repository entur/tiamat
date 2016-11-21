

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum CycleStorageEnumeration {

    RACKS("racks"),
    BARS("bars"),
    RAILINGS("railings"),
    CYCLE_SCHEME("cycleScheme"),
    OTHER("other");
    private final String value;

    CycleStorageEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CycleStorageEnumeration fromValue(String v) {
        for (CycleStorageEnumeration c: CycleStorageEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
