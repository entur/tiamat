

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum PathDirectionEnumeration {

    ONE_WAY("oneWay"),
    TWO_WAY("twoWay");
    private final String value;

    PathDirectionEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PathDirectionEnumeration fromValue(String v) {
        for (PathDirectionEnumeration c: PathDirectionEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
