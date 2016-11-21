package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class JourneyRunTimes_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<JourneyRunTime> journeyRunTime;

    public List<JourneyRunTime> getJourneyRunTime() {
        if (journeyRunTime == null) {
            journeyRunTime = new ArrayList<JourneyRunTime>();
        }
        return this.journeyRunTime;
    }

}
