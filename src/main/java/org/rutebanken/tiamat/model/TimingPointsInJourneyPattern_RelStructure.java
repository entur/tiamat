

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TimingPointsInJourneyPattern_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<TimingPointInJourneyPattern> timingPointInJourneyPattern;

    public List<TimingPointInJourneyPattern> getTimingPointInJourneyPattern() {
        if (timingPointInJourneyPattern == null) {
            timingPointInJourneyPattern = new ArrayList<TimingPointInJourneyPattern>();
        }
        return this.timingPointInJourneyPattern;
    }

}
