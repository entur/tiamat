

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class JourneyPatternRunTimes_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<Object> journeyPatternRunTimeRefOrJourneyPatternRunTime;

    public List<Object> getJourneyPatternRunTimeRefOrJourneyPatternRunTime() {
        if (journeyPatternRunTimeRefOrJourneyPatternRunTime == null) {
            journeyPatternRunTimeRefOrJourneyPatternRunTime = new ArrayList<Object>();
        }
        return this.journeyPatternRunTimeRefOrJourneyPatternRunTime;
    }

}
