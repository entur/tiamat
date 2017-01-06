package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.Duration;


public class JourneyWaitTime_VersionedChildStructure
        extends JourneyTiming_VersionedChildStructure {

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
