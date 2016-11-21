

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum EncumbranceEnumeration {

    LUGGAGE_ENCUMBERED("luggageEncumbered"),
    PUSHCHAIR("pushchair"),
    BAGGAGE_TROLLEY("baggageTrolley"),
    OVERSIZE_BAGGAGE("oversizeBaggage"),
    GUIDE_DOG("guideDog"),
    OTHER_ANIMAL("otherAnimal"),
    OTHER_ENCUMBRANCE_NEED("otherEncumbranceNeed");
    private final String value;

    EncumbranceEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EncumbranceEnumeration fromValue(String v) {
        for (EncumbranceEnumeration c: EncumbranceEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
