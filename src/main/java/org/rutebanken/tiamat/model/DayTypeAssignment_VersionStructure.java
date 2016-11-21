

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


public class DayTypeAssignment_VersionStructure
    extends Assignment_VersionStructure
{

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
