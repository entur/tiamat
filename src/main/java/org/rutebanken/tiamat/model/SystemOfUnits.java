

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum SystemOfUnits {


    SI_METRES("SiMetres"),

    SI_KILOMETERS_AND_METRES("SiKilometersAndMetres"),
    OTHER("Other");
    private final String value;

    SystemOfUnits(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SystemOfUnits fromValue(String v) {
        for (SystemOfUnits c: SystemOfUnits.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
