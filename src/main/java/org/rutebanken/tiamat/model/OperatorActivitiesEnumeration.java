

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum OperatorActivitiesEnumeration {

    PASSENGER("passenger"),
    FREIGHT("freight"),
    INFRASTRUCTURE("infrastructure"),
    OTHER("other");
    private final String value;

    OperatorActivitiesEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OperatorActivitiesEnumeration fromValue(String v) {
        for (OperatorActivitiesEnumeration c: OperatorActivitiesEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
