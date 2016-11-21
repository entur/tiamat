

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class MeetingPointService_VersionStructure
    extends CustomerService_VersionStructure
{

    protected MeetingPointEnumeration meetingPointServiceType;
    protected MultilingualStringEntity label;

    public MeetingPointEnumeration getMeetingPointServiceType() {
        return meetingPointServiceType;
    }

    public void setMeetingPointServiceType(MeetingPointEnumeration value) {
        this.meetingPointServiceType = value;
    }

    public MultilingualStringEntity getLabel() {
        return label;
    }

    public void setLabel(MultilingualStringEntity value) {
        this.label = value;
    }

}
