

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


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

    public String value() {
        return value;
    }

    public static ServicedOrganisationTypeEnumeration fromValue(String v) {
        for (ServicedOrganisationTypeEnumeration c: ServicedOrganisationTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
