package org.rutebanken.tiamat.model;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;


public class DayType_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected MultilingualStringEntity description;
    protected PrivateCodeStructure privateCode;
    protected XMLGregorianCalendar earliestTime;
    protected Duration dayLength;
    protected PropertiesOfDay_RelStructure properties;
    protected Timebands_RelStructure timebands;

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

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
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

    public PropertiesOfDay_RelStructure getProperties() {
        return properties;
    }

    public void setProperties(PropertiesOfDay_RelStructure value) {
        this.properties = value;
    }

    public Timebands_RelStructure getTimebands() {
        return timebands;
    }

    public void setTimebands(Timebands_RelStructure value) {
        this.timebands = value;
    }

}
