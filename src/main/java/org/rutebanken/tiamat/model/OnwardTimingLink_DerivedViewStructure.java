package org.rutebanken.tiamat.model;

import javax.xml.datatype.Duration;


public class OnwardTimingLink_DerivedViewStructure
        extends DerivedViewStructure {

    protected TimingLinkInJourneyPatternRefStructure timingLinkInJourneyPatternRef;
    protected TimingLinkRefStructure timingLinkRef;
    protected TimingPointRefStructure toPointRef;
    protected Duration runTime;

    public TimingLinkInJourneyPatternRefStructure getTimingLinkInJourneyPatternRef() {
        return timingLinkInJourneyPatternRef;
    }

    public void setTimingLinkInJourneyPatternRef(TimingLinkInJourneyPatternRefStructure value) {
        this.timingLinkInJourneyPatternRef = value;
    }

    public TimingLinkRefStructure getTimingLinkRef() {
        return timingLinkRef;
    }

    public void setTimingLinkRef(TimingLinkRefStructure value) {
        this.timingLinkRef = value;
    }

    public TimingPointRefStructure getToPointRef() {
        return toPointRef;
    }

    public void setToPointRef(TimingPointRefStructure value) {
        this.toPointRef = value;
    }

    public Duration getRunTime() {
        return runTime;
    }

    public void setRunTime(Duration value) {
        this.runTime = value;
    }

}
