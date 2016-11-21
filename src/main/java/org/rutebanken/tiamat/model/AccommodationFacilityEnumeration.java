

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum AccommodationFacilityEnumeration {

    UNKNOWN("unknown"),
    SEATING("seating"),
    SLEEPER("sleeper"),
    SINGLE_SLEEPER("singleSleeper"),
    DOUBLE_SLEEPER("doubleSleeper"),
    SPECIAL_SLEEPER("specialSleeper"),
    COUCHETTE("couchette"),
    SINGLE_COUCHETTE("singleCouchette"),
    DOUBLE_COUCHETTE("doubleCouchette"),
    SPECIAL_SEATING("specialSeating"),
    RECLINING_SEATS("recliningSeats"),
    BABY_COMPARTMENT("babyCompartment"),
    FAMILY_CARRIAGE("familyCarriage"),
    RECREATION_AREA("recreationArea"),
    PANORAMA_COACH("panoramaCoach"),
    PULLMAN_COACH("pullmanCoach"),
    STANDING("standing");
    private final String value;

    AccommodationFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AccommodationFacilityEnumeration fromValue(String v) {
        for (AccommodationFacilityEnumeration c: AccommodationFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
