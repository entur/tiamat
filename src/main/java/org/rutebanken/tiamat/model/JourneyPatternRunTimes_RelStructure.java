package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class JourneyPatternRunTimes_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<Object> journeyPatternRunTimeRefOrJourneyPatternRunTime;

    public List<Object> getJourneyPatternRunTimeRefOrJourneyPatternRunTime() {
        if (journeyPatternRunTimeRefOrJourneyPatternRunTime == null) {
            journeyPatternRunTimeRefOrJourneyPatternRunTime = new ArrayList<Object>();
        }
        return this.journeyPatternRunTimeRefOrJourneyPatternRunTime;
    }

}
