

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum CongestionEnumeration {

    NO_WAITING("noWaiting"),
    QUEUE("queue"),
    CROWDING("crowding"),
    FULL("full");
    private final String value;

    CongestionEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CongestionEnumeration fromValue(String v) {
        for (CongestionEnumeration c: CongestionEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
