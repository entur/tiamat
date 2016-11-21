package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class JourneyPatternRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends JourneyPatternRefStructure>> journeyPatternRef;

    public List<JAXBElement<? extends JourneyPatternRefStructure>> getJourneyPatternRef() {
        if (journeyPatternRef == null) {
            journeyPatternRef = new ArrayList<JAXBElement<? extends JourneyPatternRefStructure>>();
        }
        return this.journeyPatternRef;
    }

}
