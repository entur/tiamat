package org.rutebanken.tiamat.model;

public enum UicProductCharacteristicEnumeration {

    TARIFF_COMMUN_VOYAGEURS("tariffCommunVoyageurs"),
    ALL_I_INCLUSIVE_PRICE("allIInclusivePrice"),
    EAST_WEST_TARIFF("eastWestTariff"),
    TRAIN_WITH_TCV_AND_MARKET_PRICE("trainWithTcvAndMarketPrice"),
    NO_PUBLISHED_TARIFF("noPublishedTariff");
    private final String value;

    UicProductCharacteristicEnumeration(String v) {
        value = v;
    }

    public static UicProductCharacteristicEnumeration fromValue(String v) {
        for (UicProductCharacteristicEnumeration c : UicProductCharacteristicEnumeration.values()) {
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
