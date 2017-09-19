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


public class DayTypeAssignment_VersionStructure
        extends Assignment_VersionStructure {

    protected ServiceCalendarRefStructure serviceCalendarRef;
    protected OperatingPeriodRefStructure operatingPeriodRef;
    protected OperatingDayRefStructure operatingDayRef;
    protected XMLGregorianCalendar date;
    protected DayTypeRefStructure dayTypeRef;
    protected List<TimebandRefStructure> timebandRef;
    protected Boolean isAvailable;

    public ServiceCalendarRefStructure getServiceCalendarRef() {
        return serviceCalendarRef;
    }

    public void setServiceCalendarRef(ServiceCalendarRefStructure value) {
        this.serviceCalendarRef = value;
    }

    public OperatingPeriodRefStructure getOperatingPeriodRef() {
        return operatingPeriodRef;
    }

    public void setOperatingPeriodRef(OperatingPeriodRefStructure value) {
        this.operatingPeriodRef = value;
    }

    public OperatingDayRefStructure getOperatingDayRef() {
        return operatingDayRef;
    }

    public void setOperatingDayRef(OperatingDayRefStructure value) {
        this.operatingDayRef = value;
    }

    public XMLGregorianCalendar getDate() {
        return date;
    }

    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    public DayTypeRefStructure getDayTypeRef() {
        return dayTypeRef;
    }

    public void setDayTypeRef(DayTypeRefStructure value) {
        this.dayTypeRef = value;
    }

    public List<TimebandRefStructure> getTimebandRef() {
        if (timebandRef == null) {
            timebandRef = new ArrayList<TimebandRefStructure>();
        }
        return this.timebandRef;
    }

    public Boolean isIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean value) {
        this.isAvailable = value;
    }

}
