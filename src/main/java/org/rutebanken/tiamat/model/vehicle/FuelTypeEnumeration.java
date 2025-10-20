package org.rutebanken.tiamat.model.vehicle;

public enum FuelTypeEnumeration {
    BATTERY("battery"),
    BIODIESEL("biodiesel"),
    DIESEL("diesel"),
    DIESEL_BATTERY_HYBRID("dieselBatteryHybrid"),
    ELECTRIC_CONTACT("electricContact"),
    ELECTRICITY("electricity"),
    ETHANOL("ethanol"),
    HYDROGEN("hydrogen"),
    LIQUID_GAS("liquidGas"),
    TPG("tpg"),
    METHANE("methane"),
    NATURAL_GAS("naturalGas"),
    PETROL("petrol"),
    PETROL_BATTERY_HYBRID("petrolBatteryHybrid"),
    PETROL_LEADED("petrolLeaded"),
    PETROL_UNLEADED("petrolUnleaded"),
    NONE("none"),
    OTHER("other");

    private final String value;

    private FuelTypeEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static FuelTypeEnumeration fromValue(String v) {
        for(FuelTypeEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
