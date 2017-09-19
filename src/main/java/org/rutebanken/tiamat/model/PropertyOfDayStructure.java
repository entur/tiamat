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

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


public class PropertyOfDayStructure {

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity description;
    protected List<DayOfWeekEnumeration> daysOfWeek;
    protected List<String> weeksOfMonth;
    protected XMLGregorianCalendar monthOfYear;
    protected XMLGregorianCalendar dayOfYear;
    protected CountryRef countryRef;
    protected List<HolidayTypeEnumeration> holidayTypes;
    protected List<SeasonEnumeration> seasons;
    protected List<TideEnumeration> tides;
    protected DayEventEnumeration dayEvent;
    protected CrowdingEnumeration crowding;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public List<DayOfWeekEnumeration> getDaysOfWeek() {
        if (daysOfWeek == null) {
            daysOfWeek = new ArrayList<DayOfWeekEnumeration>();
        }
        return this.daysOfWeek;
    }

    public List<String> getWeeksOfMonth() {
        if (weeksOfMonth == null) {
            weeksOfMonth = new ArrayList<String>();
        }
        return this.weeksOfMonth;
    }

    public XMLGregorianCalendar getMonthOfYear() {
        return monthOfYear;
    }

    public void setMonthOfYear(XMLGregorianCalendar value) {
        this.monthOfYear = value;
    }

    public XMLGregorianCalendar getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(XMLGregorianCalendar value) {
        this.dayOfYear = value;
    }

    public CountryRef getCountryRef() {
        return countryRef;
    }

    public void setCountryRef(CountryRef value) {
        this.countryRef = value;
    }

    public List<HolidayTypeEnumeration> getHolidayTypes() {
        if (holidayTypes == null) {
            holidayTypes = new ArrayList<HolidayTypeEnumeration>();
        }
        return this.holidayTypes;
    }

    public List<SeasonEnumeration> getSeasons() {
        if (seasons == null) {
            seasons = new ArrayList<SeasonEnumeration>();
        }
        return this.seasons;
    }

    public List<TideEnumeration> getTides() {
        if (tides == null) {
            tides = new ArrayList<TideEnumeration>();
        }
        return this.tides;
    }

    public DayEventEnumeration getDayEvent() {
        return dayEvent;
    }

    public void setDayEvent(DayEventEnumeration value) {
        this.dayEvent = value;
    }

    public CrowdingEnumeration getCrowding() {
        return crowding;
    }

    public void setCrowding(CrowdingEnumeration value) {
        this.crowding = value;
    }

}
