

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum MoneyServiceEnumeration {

    CASH_MACHINE("cashMachine"),
    BANK("bank"),
    INSURANCE("insurance"),
    BUREAU_DE_CHANGE("bureauDeChange"),
    CUSTOMS_OFFICE("customsOffice");
    private final String value;

    MoneyServiceEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MoneyServiceEnumeration fromValue(String v) {
        for (MoneyServiceEnumeration c: MoneyServiceEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
