

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;


    "calendarDate",
    "serviceCalendarRef",
    "name",
    "shortName",
    "dayNumber",
    "privateCode",
    "earliestTime",
public class OperatingDay_VersionStructure
    extends DataManagedObjectStructure
{

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
