

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


public enum BooleanOperatorEnumeration {


    AND,
    OR,
    NOT,
    XOR;

    public String value() {
        return name();
    }

    public static BooleanOperatorEnumeration fromValue(String v) {
        return valueOf(v);
    }

}
