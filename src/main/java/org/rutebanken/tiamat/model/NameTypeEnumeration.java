

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum NameTypeEnumeration {

    ALIAS("alias"),
    TRANSLATION("translation"),
    COPY("copy"),
    LABEL("label"),
    OTHER("other");
    private final String value;

    NameTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NameTypeEnumeration fromValue(String v) {
        for (NameTypeEnumeration c: NameTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
