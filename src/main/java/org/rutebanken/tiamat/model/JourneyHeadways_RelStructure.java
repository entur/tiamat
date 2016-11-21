

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class JourneyHeadways_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<JourneyHeadway> journeyHeadway;

    public List<JourneyHeadway> getJourneyHeadway() {
        if (journeyHeadway == null) {
            journeyHeadway = new ArrayList<JourneyHeadway>();
        }
        return this.journeyHeadway;
    }

}
