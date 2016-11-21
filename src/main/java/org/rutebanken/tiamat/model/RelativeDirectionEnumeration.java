

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum RelativeDirectionEnumeration {

    BOTH("both"),
    FORWARDS("forwards"),
    BACKWARDS("backwards");
    private final String value;

    RelativeDirectionEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RelativeDirectionEnumeration fromValue(String v) {
        for (RelativeDirectionEnumeration c: RelativeDirectionEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
