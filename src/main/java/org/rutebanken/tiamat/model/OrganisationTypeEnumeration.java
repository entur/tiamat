package org.rutebanken.tiamat.model;

public enum OrganisationTypeEnumeration {

    AUTHORITY("authority"),
    OPERATOR("operator"),
    RAIL_OPERATOR("railOperator"),
    RAIL_FREIGHT_OPERATOR("railFreightOperator"),
    STATUTORY_BODY("statutoryBody"),
    FACILITY_OPERATOR("facilityOperator"),
    TRAVEL_AGENT("travelAgent"),
    SERVICED_ORGANISATION("servicedOrganisation"),
    RETAIL_CONSORTIUM("retailConsortium"),
    OTHER("other");
    private final String value;

    OrganisationTypeEnumeration(String v) {
        value = v;
    }

    public static OrganisationTypeEnumeration fromValue(String v) {
        for (OrganisationTypeEnumeration c : OrganisationTypeEnumeration.values()) {
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
