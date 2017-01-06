package org.rutebanken.tiamat.model;

public class TimingLinkInJourneyPattern_VersionedChildStructure
        extends LinkInLinkSequence_VersionedChildStructure {

    protected TimingLinkRefStructure timingLinkRef;
    protected JourneyRunTimes_RelStructure runTimes;

    public TimingLinkRefStructure getTimingLinkRef() {
        return timingLinkRef;
    }

    public void setTimingLinkRef(TimingLinkRefStructure value) {
        this.timingLinkRef = value;
    }

    public JourneyRunTimes_RelStructure getRunTimes() {
        return runTimes;
    }

    public void setRunTimes(JourneyRunTimes_RelStructure value) {
        this.runTimes = value;
    }

}
