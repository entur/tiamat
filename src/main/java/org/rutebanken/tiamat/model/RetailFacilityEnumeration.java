package org.rutebanken.tiamat.model;

public enum RetailFacilityEnumeration {

    UNKNOWN("unknown"),
    FOOD("food"),
    NEWSPAPER_TOBACCO("newspaperTobacco"),
    RECREATION_TRAVEL("recreationTravel"),
    HYGIENE_HEALTH_BEAUTY("hygieneHealthBeauty"),
    FASHION_ACCESSORIES("fashionAccessories"),
    BANK_FINANCE_INSURANCE("bankFinanceInsurance"),
    CASH_MACHINE("cashMachine"),
    CURRENCY_EXCHANGE("currencyExchange"),
    TOURISM_SERVICE("tourismService"),
    PHOTO_BOOTH("photoBooth");
    private final String value;

    RetailFacilityEnumeration(String v) {
        value = v;
    }

    public static RetailFacilityEnumeration fromValue(String v) {
        for (RetailFacilityEnumeration c : RetailFacilityEnumeration.values()) {
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
