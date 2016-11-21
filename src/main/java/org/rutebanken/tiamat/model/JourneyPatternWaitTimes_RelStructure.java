

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class JourneyPatternWaitTimes_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<Object> journeyPatternWaitTimeRefOrJourneyPatternWaitTime;

    public List<Object> getJourneyPatternWaitTimeRefOrJourneyPatternWaitTime() {
        if (journeyPatternWaitTimeRefOrJourneyPatternWaitTime == null) {
            journeyPatternWaitTimeRefOrJourneyPatternWaitTime = new ArrayList<Object>();
        }
        return this.journeyPatternWaitTimeRefOrJourneyPatternWaitTime;
    }

}
