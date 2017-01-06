package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class JourneyPatternWaitTimes_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<Object> journeyPatternWaitTimeRefOrJourneyPatternWaitTime;

    public List<Object> getJourneyPatternWaitTimeRefOrJourneyPatternWaitTime() {
        if (journeyPatternWaitTimeRefOrJourneyPatternWaitTime == null) {
            journeyPatternWaitTimeRefOrJourneyPatternWaitTime = new ArrayList<Object>();
        }
        return this.journeyPatternWaitTimeRefOrJourneyPatternWaitTime;
    }

}
