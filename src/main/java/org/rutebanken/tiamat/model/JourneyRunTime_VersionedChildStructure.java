

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


public class JourneyRunTime_VersionedChildStructure
    extends JourneyTiming_VersionedChildStructure
{

    protected TimingLinkRefStructure timingLinkRef;
    protected Duration runTime;

    public TimingLinkRefStructure getTimingLinkRef() {
        return timingLinkRef;
    }

    public void setTimingLinkRef(TimingLinkRefStructure value) {
        this.timingLinkRef = value;
    }

    public Duration getRunTime() {
        return runTime;
    }

    public void setRunTime(Duration value) {
        this.runTime = value;
    }

}
