

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "timingLinkRef",
public class TimingLinkInJourneyPattern_VersionedChildStructure
    extends LinkInLinkSequence_VersionedChildStructure
{

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
