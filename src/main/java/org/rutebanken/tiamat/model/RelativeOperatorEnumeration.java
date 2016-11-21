

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


public enum RelativeOperatorEnumeration {


    EQ,
    NE,
    GE,

    GT,
    LE,

    LT;

    public String value() {
        return name();
    }

    public static RelativeOperatorEnumeration fromValue(String v) {
        return valueOf(v);
    }

}
