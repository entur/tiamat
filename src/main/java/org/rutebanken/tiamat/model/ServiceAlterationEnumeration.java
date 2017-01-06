package org.rutebanken.tiamat.model;

public enum ServiceAlterationEnumeration {

    EXTRA_JOURNEY("extraJourney"),
    CANCELLATION("cancellation"),
    PLANNED("planned");
    private final String value;

    ServiceAlterationEnumeration(String v) {
        value = v;
    }

    public static ServiceAlterationEnumeration fromValue(String v) {
        for (ServiceAlterationEnumeration c : ServiceAlterationEnumeration.values()) {
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
