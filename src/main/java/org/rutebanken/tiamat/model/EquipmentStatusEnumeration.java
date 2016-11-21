

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum EquipmentStatusEnumeration {

    UNKNOWN("unknown"),
    AVAILABLE("available"),
    NOT_AVAILABLE("notAvailable");
    private final String value;

    EquipmentStatusEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EquipmentStatusEnumeration fromValue(String v) {
        for (EquipmentStatusEnumeration c: EquipmentStatusEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
