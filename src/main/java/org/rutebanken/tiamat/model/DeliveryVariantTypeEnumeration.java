package org.rutebanken.tiamat.model;

public enum DeliveryVariantTypeEnumeration {

    PRINTED("printed"),
    TEXT_TO_SPEECH("textToSpeech"),
    WEB("web"),
    MOBILE("mobile"),
    OTHER("other");
    private final String value;

    DeliveryVariantTypeEnumeration(String v) {
        value = v;
    }

    public static DeliveryVariantTypeEnumeration fromValue(String v) {
        for (DeliveryVariantTypeEnumeration c : DeliveryVariantTypeEnumeration.values()) {
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
