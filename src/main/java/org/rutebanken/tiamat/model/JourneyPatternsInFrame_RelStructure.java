package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class JourneyPatternsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<?>> journeyPattern_OrJourneyPatternView;

    public List<JAXBElement<?>> getJourneyPattern_OrJourneyPatternView() {
        if (journeyPattern_OrJourneyPatternView == null) {
            journeyPattern_OrJourneyPatternView = new ArrayList<JAXBElement<?>>();
        }
        return this.journeyPattern_OrJourneyPatternView;
    }

}
