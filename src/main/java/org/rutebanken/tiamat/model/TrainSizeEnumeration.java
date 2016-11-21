

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum TrainSizeEnumeration {

    NORMAL("normal"),
    SHORT("short"),
    LONG("long");
    private final String value;

    TrainSizeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TrainSizeEnumeration fromValue(String v) {
        for (TrainSizeEnumeration c: TrainSizeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
