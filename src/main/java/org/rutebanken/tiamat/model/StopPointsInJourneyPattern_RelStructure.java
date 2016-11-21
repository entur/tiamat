

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class StopPointsInJourneyPattern_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<StopPointInJourneyPattern> stopPointInJourneyPattern;

    public List<StopPointInJourneyPattern> getStopPointInJourneyPattern() {
        if (stopPointInJourneyPattern == null) {
            stopPointInJourneyPattern = new ArrayList<StopPointInJourneyPattern>();
        }
        return this.stopPointInJourneyPattern;
    }

}
