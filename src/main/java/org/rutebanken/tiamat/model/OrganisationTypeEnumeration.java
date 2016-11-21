

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


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

    public String value() {
        return value;
    }

    public static OrganisationTypeEnumeration fromValue(String v) {
        for (OrganisationTypeEnumeration c: OrganisationTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
