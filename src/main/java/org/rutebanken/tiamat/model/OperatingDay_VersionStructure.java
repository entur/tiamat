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

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;


public class OperatingDay_VersionStructure
        extends DataManagedObjectStructure {

    protected XMLGregorianCalendar calendarDate;
    protected ServiceCalendarRefStructure serviceCalendarRef;
    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected BigInteger dayNumber;
    protected PrivateCodeStructure privateCode;
    protected XMLGregorianCalendar earliestTime;
    protected Duration dayLength;

    public XMLGregorianCalendar getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(XMLGregorianCalendar value) {
        this.calendarDate = value;
    }

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

    public BigInteger getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(BigInteger value) {
        this.dayNumber = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public XMLGregorianCalendar getEarliestTime() {
        return earliestTime;
    }

    public void setEarliestTime(XMLGregorianCalendar value) {
        this.earliestTime = value;
    }

    public Duration getDayLength() {
        return dayLength;
    }

    public void setDayLength(Duration value) {
        this.dayLength = value;
    }

}
