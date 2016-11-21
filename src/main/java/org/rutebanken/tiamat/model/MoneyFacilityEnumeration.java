

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum MoneyFacilityEnumeration {

    OTHER("other"),
    CASH_MACHINE("cashMachine"),
    BANK("bank"),
    INSURANCE("insurance"),
    BUREAU_DE_CHANGE("bureauDeChange");
    private final String value;

    MoneyFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MoneyFacilityEnumeration fromValue(String v) {
        for (MoneyFacilityEnumeration c: MoneyFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
