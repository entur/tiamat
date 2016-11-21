

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum ViaTypeEnumeration {

    STOP_POINT("stopPoint"),
    NAME("name"),
    OTHER("other");
    private final String value;

    ViaTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ViaTypeEnumeration fromValue(String v) {
        for (ViaTypeEnumeration c: ViaTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
