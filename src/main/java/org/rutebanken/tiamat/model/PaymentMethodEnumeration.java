package org.rutebanken.tiamat.model;

public enum PaymentMethodEnumeration {

    CASH("cash"),
    CASH_AND_CARD("cashAndCard"),
    COIN("coin"),
    BANKNOTE("banknote"),
    CHEQUE("cheque"),
    TRAVELLERS_CHEQUE("travellersCheque"),
    POSTAL_ORDER("postalOrder"),
    COMPANY_CHEQUE("companyCheque"),
    CREDIT_CARD("creditCard"),
    DEBIT_CARD("debitCard"),
    CARDS_ONLY("cardsOnly"),
    TRAVEL_CARD("travelCard"),
    CONTACTLESS_PAYMENT_CARD("contactlessPaymentCard"),
    CONTACTLESS_TRAVEL_CARD("contactlessTravelCard"),
    SMS("sms"),
    MOBILE_PHONE("mobilePhone"),
    VOUCHER("voucher"),
    TOKEN("token"),
    WARRANT("warrant"),
    OTHER("other");
    private final String value;

    PaymentMethodEnumeration(String v) {
        value = v;
    }

    public static PaymentMethodEnumeration fromValue(String v) {
        for (PaymentMethodEnumeration c : PaymentMethodEnumeration.values()) {
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
