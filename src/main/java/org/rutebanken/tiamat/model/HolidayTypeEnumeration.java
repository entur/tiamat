

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum HolidayTypeEnumeration {

    ANY_DAY("AnyDay"),
    WORKING_DAY("WorkingDay"),
    SCHOOL_DAY("SchoolDay"),
    NOT_HOLIDAY("NotHoliday"),
    NOT_WORKING_DAY("NotWorkingDay"),
    NOT_SCHOOL_DAY("NotSchoolDay"),
    ANY_HOLIDAY("AnyHoliday"),
    LOCAL_HOLIDAY("LocalHoliday"),
    REGIONAL_HOLIDAY("RegionalHoliday"),
    NATIONAL_HOLIDAY("NationalHoliday"),
    HOLIDAY_DISPLACEMENT_DAY("HolidayDisplacementDay"),
    EVE_OF_HOLIDAY("EveOfHoliday");
    private final String value;

    HolidayTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HolidayTypeEnumeration fromValue(String v) {
        for (HolidayTypeEnumeration c: HolidayTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
