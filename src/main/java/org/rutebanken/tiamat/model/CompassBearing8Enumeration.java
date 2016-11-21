

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


public enum CompassBearing8Enumeration {

    SW,
    SE,
    NW,
    NE,
    W,
    E,
    S,
    N;

    public String value() {
        return name();
    }

    public static CompassBearing8Enumeration fromValue(String v) {
        return valueOf(v);
    }

}
