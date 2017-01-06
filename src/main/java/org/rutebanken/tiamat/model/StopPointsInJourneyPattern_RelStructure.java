package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class StopPointsInJourneyPattern_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<StopPointInJourneyPattern> stopPointInJourneyPattern;

    public List<StopPointInJourneyPattern> getStopPointInJourneyPattern() {
        if (stopPointInJourneyPattern == null) {
            stopPointInJourneyPattern = new ArrayList<StopPointInJourneyPattern>();
        }
        return this.stopPointInJourneyPattern;
    }

}
