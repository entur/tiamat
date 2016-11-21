

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


public class JourneyPatternsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<?>> journeyPattern_OrJourneyPatternView;

    public List<JAXBElement<?>> getJourneyPattern_OrJourneyPatternView() {
        if (journeyPattern_OrJourneyPatternView == null) {
            journeyPattern_OrJourneyPatternView = new ArrayList<JAXBElement<?>>();
        }
        return this.journeyPattern_OrJourneyPatternView;
    }

}
