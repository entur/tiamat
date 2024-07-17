package org.rutebanken.tiamat.model;

public enum StopPlaceOrganisationRelationshipEnumeration {
    OWNER("owner"),
    MAINTENANCE("maintenance"),
    WINTER_MAINTENANCE("winterMaintenance"),
    INFO_UPKEEP("infoUpkeep"),
    CLEANING("cleaning");
    private final String value;

    StopPlaceOrganisationRelationshipEnumeration(String v) {
        value = v;
    }

    public static StopPlaceOrganisationRelationshipEnumeration fromValue(String v) {
        for (StopPlaceOrganisationRelationshipEnumeration c : StopPlaceOrganisationRelationshipEnumeration.values()) {
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
