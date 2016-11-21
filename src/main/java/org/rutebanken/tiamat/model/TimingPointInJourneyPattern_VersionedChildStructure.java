

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


public class TimingPointInJourneyPattern_VersionedChildStructure
    extends PointInLinkSequence_VersionedChildStructure
{

    protected JAXBElement<? extends TimingPointRefStructure> timingPointRef;
    protected TimingLinkRefStructure onwardTimingLinkRef;
    protected Boolean isWaitPoint;
    protected Duration waitTime;
    protected JourneyPatternWaitTimes_RelStructure waitTimes;
    protected JourneyPatternHeadways_RelStructure headways;
    protected NoticeAssignments_RelStructure noticeAssignments;

    public JAXBElement<? extends TimingPointRefStructure> getTimingPointRef() {
        return timingPointRef;
    }

    public void setTimingPointRef(JAXBElement<? extends TimingPointRefStructure> value) {
        this.timingPointRef = value;
    }

    public TimingLinkRefStructure getOnwardTimingLinkRef() {
        return onwardTimingLinkRef;
    }

    public void setOnwardTimingLinkRef(TimingLinkRefStructure value) {
        this.onwardTimingLinkRef = value;
    }

    public Boolean isIsWaitPoint() {
        return isWaitPoint;
    }

    public void setIsWaitPoint(Boolean value) {
        this.isWaitPoint = value;
    }

    public Duration getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Duration value) {
        this.waitTime = value;
    }

    public JourneyPatternWaitTimes_RelStructure getWaitTimes() {
        return waitTimes;
    }

    public void setWaitTimes(JourneyPatternWaitTimes_RelStructure value) {
        this.waitTimes = value;
    }

    public JourneyPatternHeadways_RelStructure getHeadways() {
        return headways;
    }

    public void setHeadways(JourneyPatternHeadways_RelStructure value) {
        this.headways = value;
    }

    public NoticeAssignments_RelStructure getNoticeAssignments() {
        return noticeAssignments;
    }

    public void setNoticeAssignments(NoticeAssignments_RelStructure value) {
        this.noticeAssignments = value;
    }

}
