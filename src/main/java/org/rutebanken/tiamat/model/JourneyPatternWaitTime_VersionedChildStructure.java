

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class JourneyPatternWaitTime_VersionedChildStructure
    extends JourneyWaitTime_VersionedChildStructure
{

    protected JAXBElement<? extends JourneyPatternRefStructure> journeyPatternRef;

    public JAXBElement<? extends JourneyPatternRefStructure> getJourneyPatternRef() {
        return journeyPatternRef;
    }

    public void setJourneyPatternRef(JAXBElement<? extends JourneyPatternRefStructure> value) {
        this.journeyPatternRef = value;
    }

}
