

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


    "timingPointRef",
public class JourneyWaitTime_VersionedChildStructure
    extends JourneyTiming_VersionedChildStructure
{

    protected JAXBElement<? extends TimingPointRefStructure> timingPointRef;
    protected Duration waitTime;

    public JAXBElement<? extends TimingPointRefStructure> getTimingPointRef() {
        return timingPointRef;
    }

    public void setTimingPointRef(JAXBElement<? extends TimingPointRefStructure> value) {
        this.timingPointRef = value;
    }

    public Duration getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Duration value) {
        this.waitTime = value;
    }

}
