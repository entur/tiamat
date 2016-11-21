

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum RetailServiceEnumeration {

    FOOD("food"),
    HEALTH_HYGIENE_BEAUTY("healthHygieneBeauty"),
    NEWSPAPER_TOBACCO("newspaperTobacco"),
    FASHION_ACCESSORIES("fashionAccessories"),
    BANK_FINANCE_INSURANCE("bankFinanceInsurance"),
    TOURISM("tourism"),
    PHOTO_BOOTH("photoBooth");
    private final String value;

    RetailServiceEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RetailServiceEnumeration fromValue(String v) {
        for (RetailServiceEnumeration c: RetailServiceEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
