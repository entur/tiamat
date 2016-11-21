

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class JourneyPatternHeadway_VersionedChildStructure
    extends JourneyHeadway_VersionedChildStructure
{

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
