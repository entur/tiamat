package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class JourneyHeadways_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<JourneyHeadway> journeyHeadway;

    public List<JourneyHeadway> getJourneyHeadway() {
        if (journeyHeadway == null) {
            journeyHeadway = new ArrayList<JourneyHeadway>();
        }
        return this.journeyHeadway;
    }

}
