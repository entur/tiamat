package org.rutebanken.tiamat.model;

import javax.xml.datatype.Duration;
import java.math.BigDecimal;


public class OnwardServiceLink_DerivedViewStructure
        extends DerivedViewStructure {

    protected ServiceLinkInJourneyPattern_VersionedChildStructure serviceLinkInJourneyPattern;
    protected ServiceLinkRefStructure serviceLinkRef;
    protected ScheduledStopPointRefStructure toPointRef;
    protected BigDecimal distance;
    protected Duration runTime;

    public ServiceLinkInJourneyPattern_VersionedChildStructure getServiceLinkInJourneyPattern() {
        return serviceLinkInJourneyPattern;
    }

    public void setServiceLinkInJourneyPattern(ServiceLinkInJourneyPattern_VersionedChildStructure value) {
        this.serviceLinkInJourneyPattern = value;
    }

    public ServiceLinkRefStructure getServiceLinkRef() {
        return serviceLinkRef;
    }

    public void setServiceLinkRef(ServiceLinkRefStructure value) {
        this.serviceLinkRef = value;
    }

    public ScheduledStopPointRefStructure getToPointRef() {
        return toPointRef;
    }

    public void setToPointRef(ScheduledStopPointRefStructure value) {
        this.toPointRef = value;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal value) {
        this.distance = value;
    }

    public Duration getRunTime() {
        return runTime;
    }

    public void setRunTime(Duration value) {
        this.runTime = value;
    }

}
