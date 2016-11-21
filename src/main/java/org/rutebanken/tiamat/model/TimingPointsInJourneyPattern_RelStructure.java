package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TimingPointsInJourneyPattern_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<TimingPointInJourneyPattern> timingPointInJourneyPattern;

    public List<TimingPointInJourneyPattern> getTimingPointInJourneyPattern() {
        if (timingPointInJourneyPattern == null) {
            timingPointInJourneyPattern = new ArrayList<TimingPointInJourneyPattern>();
        }
        return this.timingPointInJourneyPattern;
    }

}
