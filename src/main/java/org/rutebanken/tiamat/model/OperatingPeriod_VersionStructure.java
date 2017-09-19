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


public class OperatingPeriod_VersionStructure
        extends DataManagedObjectStructure {

    protected ServiceCalendarRefStructure serviceCalendarRef;
    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected OperatingDayRefStructure fromOperatingDayRef;
    protected XMLGregorianCalendar fromDate;
    protected OperatingDayRefStructure toOperatingDayRef;
    protected XMLGregorianCalendar toDate;
    protected List<HolidayTypeEnumeration> holidayType;
    protected List<SeasonEnumeration> season;

    public ServiceCalendarRefStructure getServiceCalendarRef() {
        return serviceCalendarRef;
    }

    public void setServiceCalendarRef(ServiceCalendarRefStructure value) {
        this.serviceCalendarRef = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public OperatingDayRefStructure getFromOperatingDayRef() {
        return fromOperatingDayRef;
    }

    public void setFromOperatingDayRef(OperatingDayRefStructure value) {
        this.fromOperatingDayRef = value;
    }

    public XMLGregorianCalendar getFromDate() {
        return fromDate;
    }

    public void setFromDate(XMLGregorianCalendar value) {
        this.fromDate = value;
    }

    public OperatingDayRefStructure getToOperatingDayRef() {
        return toOperatingDayRef;
    }

    public void setToOperatingDayRef(OperatingDayRefStructure value) {
        this.toOperatingDayRef = value;
    }

    public XMLGregorianCalendar getToDate() {
        return toDate;
    }

    public void setToDate(XMLGregorianCalendar value) {
        this.toDate = value;
    }

    public List<HolidayTypeEnumeration> getHolidayType() {
        if (holidayType == null) {
            holidayType = new ArrayList<HolidayTypeEnumeration>();
        }
        return this.holidayType;
    }

    public List<SeasonEnumeration> getSeason() {
        if (season == null) {
            season = new ArrayList<SeasonEnumeration>();
        }
        return this.season;
    }

}
