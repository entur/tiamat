

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum ZoneTopologyEnumeration {

    OVERLAPPING("overlapping"),
    HONEYCOMB("honeycomb"),
    RING("ring"),
    NESTED("nested"),
    OTHER("other");
    private final String value;

    ZoneTopologyEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ZoneTopologyEnumeration fromValue(String v) {
        for (ZoneTopologyEnumeration c: ZoneTopologyEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
