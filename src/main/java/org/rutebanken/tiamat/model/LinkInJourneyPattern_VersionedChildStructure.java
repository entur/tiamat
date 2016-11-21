package org.rutebanken.tiamat.model;

public class LinkInJourneyPattern_VersionedChildStructure
        extends LinkInLinkSequence_VersionedChildStructure {

    protected ServiceLinkRefStructure serviceLinkRef;
    protected TimingLinkRefStructure timingLinkRef;

    public ServiceLinkRefStructure getServiceLinkRef() {
        return serviceLinkRef;
    }

    public void setServiceLinkRef(ServiceLinkRefStructure value) {
        this.serviceLinkRef = value;
    }

    public TimingLinkRefStructure getTimingLinkRef() {
        return timingLinkRef;
    }

    public void setTimingLinkRef(TimingLinkRefStructure value) {
        this.timingLinkRef = value;
    }

}
