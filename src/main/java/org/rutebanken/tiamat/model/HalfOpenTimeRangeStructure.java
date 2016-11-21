

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


public class HalfOpenTimeRangeStructure {

    protected XMLGregorianCalendar startTime;
    protected XMLGregorianCalendar endTime;

    public XMLGregorianCalendar getStartTime() {
        return startTime;
    }

    public void setStartTime(XMLGregorianCalendar value) {
        this.startTime = value;
    }

    public XMLGregorianCalendar getEndTime() {
        return endTime;
    }

    public void setEndTime(XMLGregorianCalendar value) {
        this.endTime = value;
    }

}
