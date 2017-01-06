package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class JourneyPatternWaitTime_VersionedChildStructure
        extends JourneyWaitTime_VersionedChildStructure {

    protected JAXBElement<? extends JourneyPatternRefStructure> journeyPatternRef;

    public JAXBElement<? extends JourneyPatternRefStructure> getJourneyPatternRef() {
        return journeyPatternRef;
    }

    public void setJourneyPatternRef(JAXBElement<? extends JourneyPatternRefStructure> value) {
        this.journeyPatternRef = value;
    }

}
