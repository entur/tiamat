package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.Duration;


public class StopPointInJourneyPattern_VersionedChildStructure
        extends PointInLinkSequence_VersionedChildStructure {

    protected JAXBElement<? extends ScheduledStopPointRefStructure> scheduledStopPointRef;
    protected TimingLinkRefStructure onwardTimingLinkRef;
    protected Boolean isWaitPoint;
    protected Duration waitTime;
    protected JourneyPatternWaitTimes_RelStructure waitTimes;
    protected JourneyPatternHeadways_RelStructure headways;
    protected ServiceLinkRefStructure onwardServiceLinkRef;
    protected Boolean forAlighting;
    protected Boolean forBoarding;
    protected DestinationDisplayRefStructure destinationDisplayRef;
    protected DestinationDisplayView destinationDisplayView;
    protected Vias_RelStructure vias;
    protected FlexiblePointProperties flexiblePointProperties;
    protected Boolean changeOfDestinationDisplay;
    protected Boolean changeOfServiceRequirements;
    protected NoticeAssignments noticeAssignments;
    protected Boolean requestStop;
    protected StopUseEnumeration stopUse;

    public JAXBElement<? extends ScheduledStopPointRefStructure> getScheduledStopPointRef() {
        return scheduledStopPointRef;
    }

    public void setScheduledStopPointRef(JAXBElement<? extends ScheduledStopPointRefStructure> value) {
        this.scheduledStopPointRef = value;
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

    public ServiceLinkRefStructure getOnwardServiceLinkRef() {
        return onwardServiceLinkRef;
    }

    public void setOnwardServiceLinkRef(ServiceLinkRefStructure value) {
        this.onwardServiceLinkRef = value;
    }

    public Boolean isForAlighting() {
        return forAlighting;
    }

    public void setForAlighting(Boolean value) {
        this.forAlighting = value;
    }

    public Boolean isForBoarding() {
        return forBoarding;
    }

    public void setForBoarding(Boolean value) {
        this.forBoarding = value;
    }

    public DestinationDisplayRefStructure getDestinationDisplayRef() {
        return destinationDisplayRef;
    }

    public void setDestinationDisplayRef(DestinationDisplayRefStructure value) {
        this.destinationDisplayRef = value;
    }

    public DestinationDisplayView getDestinationDisplayView() {
        return destinationDisplayView;
    }

    public void setDestinationDisplayView(DestinationDisplayView value) {
        this.destinationDisplayView = value;
    }

    public Vias_RelStructure getVias() {
        return vias;
    }

    public void setVias(Vias_RelStructure value) {
        this.vias = value;
    }

    public FlexiblePointProperties getFlexiblePointProperties() {
        return flexiblePointProperties;
    }

    public void setFlexiblePointProperties(FlexiblePointProperties value) {
        this.flexiblePointProperties = value;
    }

    public Boolean isChangeOfDestinationDisplay() {
        return changeOfDestinationDisplay;
    }

    public void setChangeOfDestinationDisplay(Boolean value) {
        this.changeOfDestinationDisplay = value;
    }

    public Boolean isChangeOfServiceRequirements() {
        return changeOfServiceRequirements;
    }

    public void setChangeOfServiceRequirements(Boolean value) {
        this.changeOfServiceRequirements = value;
    }

    public NoticeAssignments getNoticeAssignments() {
        return noticeAssignments;
    }

    public void setNoticeAssignments(NoticeAssignments value) {
        this.noticeAssignments = value;
    }

    public Boolean isRequestStop() {
        return requestStop;
    }

    public void setRequestStop(Boolean value) {
        this.requestStop = value;
    }

    public StopUseEnumeration getStopUse() {
        return stopUse;
    }

    public void setStopUse(StopUseEnumeration value) {
        this.stopUse = value;
    }

}
