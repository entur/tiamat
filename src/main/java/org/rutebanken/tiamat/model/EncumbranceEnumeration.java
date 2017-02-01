package org.rutebanken.tiamat.model;

public enum EncumbranceEnumeration {

    LUGGAGE_ENCUMBERED("luggageEncumbered"),
    PUSHCHAIR("pushchair"),
    BAGGAGE_TROLLEY("baggageTrolley"),
    OVERSIZE_BAGGAGE("oversizeBaggage"),
    GUIDE_DOG("guideDog"),
    OTHER_ANIMAL("otherAnimal"),
    OTHER_ENCUMBRANCE_NEED("otherEncumbranceNeed");
    private final String value;

    EncumbranceEnumeration(String v) {
        value = v;
    }

    public static EncumbranceEnumeration fromValue(String v) {
        for (EncumbranceEnumeration c : EncumbranceEnumeration.values()) {
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
