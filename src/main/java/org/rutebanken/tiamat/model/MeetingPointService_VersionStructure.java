package org.rutebanken.tiamat.model;

public class MeetingPointService_VersionStructure
        extends CustomerService_VersionStructure {

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
