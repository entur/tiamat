package org.rutebanken.tiamat.model;

public enum StakeholderRoleTypeEnumeration {

    PLANNING("Planning"),
    OPERATION("Operation"),
    CONTROL("Control"),
    RESERVATION("Reservation"),
    ENTITY_LEGAL_OWNERSHIP("EntityLegalOwnership"),
    OTHER("Other");
    private final String value;

    StakeholderRoleTypeEnumeration(String v) {
        value = v;
    }

    public static StakeholderRoleTypeEnumeration fromValue(String v) {
        for (StakeholderRoleTypeEnumeration c : StakeholderRoleTypeEnumeration.values()) {
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
