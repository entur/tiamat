/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

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

    public static HolidayTypeEnumeration fromValue(String v) {
        for (HolidayTypeEnumeration c : HolidayTypeEnumeration.values()) {
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
