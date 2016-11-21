

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum LanguageUseEnumeration {

    NORMALLY_USED("normallyUsed"),
    UNDERSTOOD("understood"),
    NATIVE("native"),
    SPOKEN("spoken"),
    WRITTEN("written"),
    READ("read"),
    OTHER("other"),
    ALL_USES("allUses");
    private final String value;

    LanguageUseEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LanguageUseEnumeration fromValue(String v) {
        for (LanguageUseEnumeration c: LanguageUseEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
