

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum CouchetteFacilityEnumeration {

    UNKNOWN("unknown"),
    T_2("T2"),
    T_3("T3"),
    C_1("C1"),
    C_2("C2"),
    C_4("C4"),

    C_5("C5"),
    C_6("C6"),
    WHEELCHAIR("wheelchair"),
    OTHER("other");
    private final String value;

    CouchetteFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CouchetteFacilityEnumeration fromValue(String v) {
        for (CouchetteFacilityEnumeration c: CouchetteFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
