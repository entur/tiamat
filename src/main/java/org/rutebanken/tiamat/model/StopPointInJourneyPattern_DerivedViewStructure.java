

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


public class StopPointInJourneyPattern_DerivedViewStructure
    extends DerivedViewStructure
{

    protected JAXBElement<? extends PointInJourneyPatternRefStructure> pointInJourneyPatternRef;
    protected BigInteger visitNumber;
    protected JAXBElement<? extends ScheduledStopPointRefStructure> scheduledStopPointRef;
    protected ScheduledStopPoint_DerivedViewStructure scheduledStopPointView;
    protected OnwardTimingLinkView onwardTimingLinkView;
    protected ServiceLinkRefStructure onwardServiceLinkRef;
    protected OnwardServiceLinkView onwardServiceLinkView;
    protected TimingPointStatusEnumeration timingPointStatus;
    protected Boolean isWaitPoint;
    protected TimeDemandTypeRefStructure timeDemandTypeRef;
    protected TimebandRefStructure timebandRef;
    protected Duration waitTime;
    protected Duration scheduledHeadwayInterval;
    protected Duration minimumHeadwayInterval;
    protected Duration maximumHeadwayInterval;
    protected BigInteger order;

    public JAXBElement<? extends PointInJourneyPatternRefStructure> getPointInJourneyPatternRef() {
        return pointInJourneyPatternRef;
    }

    public void setPointInJourneyPatternRef(JAXBElement<? extends PointInJourneyPatternRefStructure> value) {
        this.pointInJourneyPatternRef = value;
    }

    public BigInteger getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(BigInteger value) {
        this.visitNumber = value;
    }

    public JAXBElement<? extends ScheduledStopPointRefStructure> getScheduledStopPointRef() {
        return scheduledStopPointRef;
    }

    public void setScheduledStopPointRef(JAXBElement<? extends ScheduledStopPointRefStructure> value) {
        this.scheduledStopPointRef = value;
    }

    public ScheduledStopPoint_DerivedViewStructure getScheduledStopPointView() {
        return scheduledStopPointView;
    }

    public void setScheduledStopPointView(ScheduledStopPoint_DerivedViewStructure value) {
        this.scheduledStopPointView = value;
    }

    public OnwardTimingLinkView getOnwardTimingLinkView() {
        return onwardTimingLinkView;
    }

    public void setOnwardTimingLinkView(OnwardTimingLinkView value) {
        this.onwardTimingLinkView = value;
    }

    public ServiceLinkRefStructure getOnwardServiceLinkRef() {
        return onwardServiceLinkRef;
    }

    public void setOnwardServiceLinkRef(ServiceLinkRefStructure value) {
        this.onwardServiceLinkRef = value;
    }

    public OnwardServiceLinkView getOnwardServiceLinkView() {
        return onwardServiceLinkView;
    }

    public void setOnwardServiceLinkView(OnwardServiceLinkView value) {
        this.onwardServiceLinkView = value;
    }

    public TimingPointStatusEnumeration getTimingPointStatus() {
        return timingPointStatus;
    }

    public void setTimingPointStatus(TimingPointStatusEnumeration value) {
        this.timingPointStatus = value;
    }

    public Boolean isIsWaitPoint() {
        return isWaitPoint;
    }

    public void setIsWaitPoint(Boolean value) {
        this.isWaitPoint = value;
    }

    public TimeDemandTypeRefStructure getTimeDemandTypeRef() {
        return timeDemandTypeRef;
    }

    public void setTimeDemandTypeRef(TimeDemandTypeRefStructure value) {
        this.timeDemandTypeRef = value;
    }

    public TimebandRefStructure getTimebandRef() {
        return timebandRef;
    }

    public void setTimebandRef(TimebandRefStructure value) {
        this.timebandRef = value;
    }

    public Duration getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Duration value) {
        this.waitTime = value;
    }

    public Duration getScheduledHeadwayInterval() {
        return scheduledHeadwayInterval;
    }

    public void setScheduledHeadwayInterval(Duration value) {
        this.scheduledHeadwayInterval = value;
    }

    public Duration getMinimumHeadwayInterval() {
        return minimumHeadwayInterval;
    }

    public void setMinimumHeadwayInterval(Duration value) {
        this.minimumHeadwayInterval = value;
    }

    public Duration getMaximumHeadwayInterval() {
        return maximumHeadwayInterval;
    }

    public void setMaximumHeadwayInterval(Duration value) {
        this.maximumHeadwayInterval = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}
