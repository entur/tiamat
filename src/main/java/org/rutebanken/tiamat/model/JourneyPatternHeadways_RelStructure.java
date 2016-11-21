package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class JourneyPatternHeadways_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<JourneyPatternHeadway> journeyPatternHeadway;

    public List<JourneyPatternHeadway> getJourneyPatternHeadway() {
        if (journeyPatternHeadway == null) {
            journeyPatternHeadway = new ArrayList<JourneyPatternHeadway>();
        }
        return this.journeyPatternHeadway;
    }

}
