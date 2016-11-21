

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum MandatoryEnumeration {


    REQUIRED("required"),

    OPTIONAL("optional"),

    NOT_ALLOWED("notAllowed");
    private final String value;

    MandatoryEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MandatoryEnumeration fromValue(String v) {
        for (MandatoryEnumeration c: MandatoryEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
