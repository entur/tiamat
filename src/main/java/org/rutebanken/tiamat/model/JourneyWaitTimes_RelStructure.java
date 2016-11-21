package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class JourneyWaitTimes_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<JourneyWaitTime> journeyWaitTime;

    public List<JourneyWaitTime> getJourneyWaitTime() {
        if (journeyWaitTime == null) {
            journeyWaitTime = new ArrayList<JourneyWaitTime>();
        }
        return this.journeyWaitTime;
    }

}
