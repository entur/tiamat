

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class JourneyLayovers_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<JourneyLayover> journeyLayover;

    public List<JourneyLayover> getJourneyLayover() {
        if (journeyLayover == null) {
            journeyLayover = new ArrayList<JourneyLayover>();
        }
        return this.journeyLayover;
    }

}
