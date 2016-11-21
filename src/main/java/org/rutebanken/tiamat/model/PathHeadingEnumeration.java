

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum PathHeadingEnumeration {

    LEFT("left"),
    RIGHT("right"),
    FORWARD("forward"),
    BACK("back");
    private final String value;

    PathHeadingEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PathHeadingEnumeration fromValue(String v) {
        for (PathHeadingEnumeration c: PathHeadingEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
