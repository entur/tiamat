

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum UicRateTypeEnumeration {

    NORMAL("normal"),
    DISCOUNT_IN_TRAIN_OTHER_THAN_TGV("discountInTrainOtherThanTGV"),
    SPECIAL_FARE("specialFare"),
    SUPPLEMENT("supplement"),
    NO_PUBLISHED_TARIFF("noPublishedTariff");
    private final String value;

    UicRateTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UicRateTypeEnumeration fromValue(String v) {
        for (UicRateTypeEnumeration c: UicRateTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
