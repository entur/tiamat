package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class JourneyPatternHeadway_VersionedChildStructure
        extends JourneyHeadway_VersionedChildStructure {

    protected JAXBElement<? extends JourneyPatternRefStructure> journeyPatternRef;
    protected JAXBElement<? extends TimingPointRefStructure> timingPointRef;

    public JAXBElement<? extends JourneyPatternRefStructure> getJourneyPatternRef() {
        return journeyPatternRef;
    }

    public void setJourneyPatternRef(JAXBElement<? extends JourneyPatternRefStructure> value) {
        this.journeyPatternRef = value;
    }

    public JAXBElement<? extends TimingPointRefStructure> getTimingPointRef() {
        return timingPointRef;
    }

    public void setTimingPointRef(JAXBElement<? extends TimingPointRefStructure> value) {
        this.timingPointRef = value;
    }

}
