

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


public class Timeband_VersionedChildStructure
    extends DataManagedObjectStructure
{

    protected MultilingualStringEntity name;
    protected XMLGregorianCalendar startTime;
    protected XMLGregorianCalendar endTime;
    protected BigInteger dayOffset;
    protected Duration duration;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

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

    public BigInteger getDayOffset() {
        return dayOffset;
    }

    public void setDayOffset(BigInteger value) {
        this.dayOffset = value;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration value) {
        this.duration = value;
    }

}
