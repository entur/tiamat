

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class JourneyPatternLayovers_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<JourneyPatternLayover> journeyPatternLayover;

    public List<JourneyPatternLayover> getJourneyPatternLayover() {
        if (journeyPatternLayover == null) {
            journeyPatternLayover = new ArrayList<JourneyPatternLayover>();
        }
        return this.journeyPatternLayover;
    }

}
