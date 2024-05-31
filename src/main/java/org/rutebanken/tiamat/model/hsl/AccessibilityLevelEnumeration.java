package org.rutebanken.tiamat.model.hsl;

public enum AccessibilityLevelEnumeration {
    FULLY_ACCESSIBLE("fullyAccessible"),
    MOSTLY_ACCESSIBLE("mostlyAccessible"),
    PARTIALLY_INACCESSIBLE("partiallyInaccessible"),
    INACCESSIBLE("inaccessible"),
    UNKNOWN("unknown");
    private final String value;

    AccessibilityLevelEnumeration(String v) {
        value = v;
    }

    public static AccessibilityLevelEnumeration fromValue(String value) {
        for (AccessibilityLevelEnumeration enumeration : AccessibilityLevelEnumeration.values()) {
            if (enumeration.value.equals(value)) {
                return enumeration;
            }
        }
        throw new IllegalArgumentException(value);
    }

    public String value() {
        return value;
    }
}
