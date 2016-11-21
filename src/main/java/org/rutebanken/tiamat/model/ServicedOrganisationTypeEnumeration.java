package org.rutebanken.tiamat.model;

public enum ServicedOrganisationTypeEnumeration {

    SCHOOL("school"),
    COLLEGE("college"),
    UNIVERSITY("university"),
    MILITARY_BASE("militaryBase"),
    WORKS("works"),
    RETAIIL_CENTRE("retaiilCentre"),
    HOSPITAL("hospital"),
    GOVERNMENT_OFFICE("governmentOffice"),
    OTHER("other");
    private final String value;

    ServicedOrganisationTypeEnumeration(String v) {
        value = v;
    }

    public static ServicedOrganisationTypeEnumeration fromValue(String v) {
        for (ServicedOrganisationTypeEnumeration c : ServicedOrganisationTypeEnumeration.values()) {
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
