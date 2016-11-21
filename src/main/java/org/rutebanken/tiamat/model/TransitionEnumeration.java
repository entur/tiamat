

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum TransitionEnumeration {

    UP("up"),
    DOWN("down"),
    LEVEL("level"),
    UP_AND_DOWN("upAndDown"),
    DOWN_AND_UP("downAndUp");
    private final String value;

    TransitionEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TransitionEnumeration fromValue(String v) {
        for (TransitionEnumeration c: TransitionEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
