

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class JourneyWaitTimes_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<JourneyWaitTime> journeyWaitTime;

    public List<JourneyWaitTime> getJourneyWaitTime() {
        if (journeyWaitTime == null) {
            journeyWaitTime = new ArrayList<JourneyWaitTime>();
        }
        return this.journeyWaitTime;
    }

}
