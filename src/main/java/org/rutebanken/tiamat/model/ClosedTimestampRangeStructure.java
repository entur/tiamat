package org.rutebanken.tiamat.model;

import javax.xml.datatype.XMLGregorianCalendar;


public class ClosedTimestampRangeStructure {

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
