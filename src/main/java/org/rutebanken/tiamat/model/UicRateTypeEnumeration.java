package org.rutebanken.tiamat.model;

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

    public static UicRateTypeEnumeration fromValue(String v) {
        for (UicRateTypeEnumeration c : UicRateTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String value() {
        return value;
    }

}
