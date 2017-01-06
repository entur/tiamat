package org.rutebanken.tiamat.model;

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

    public static MoneyFacilityEnumeration fromValue(String v) {
        for (MoneyFacilityEnumeration c : MoneyFacilityEnumeration.values()) {
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
