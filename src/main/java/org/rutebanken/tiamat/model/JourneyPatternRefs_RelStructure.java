

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class JourneyPatternRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends JourneyPatternRefStructure>> journeyPatternRef;

    public List<JAXBElement<? extends JourneyPatternRefStructure>> getJourneyPatternRef() {
        if (journeyPatternRef == null) {
            journeyPatternRef = new ArrayList<JAXBElement<? extends JourneyPatternRefStructure>>();
        }
        return this.journeyPatternRef;
    }

}
