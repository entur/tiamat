

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class LinkInJourneyPattern_VersionedChildStructure
    extends LinkInLinkSequence_VersionedChildStructure
{

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
