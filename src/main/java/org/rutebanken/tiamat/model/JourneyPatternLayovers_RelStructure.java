package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class JourneyPatternLayovers_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<JourneyPatternLayover> journeyPatternLayover;

    public List<JourneyPatternLayover> getJourneyPatternLayover() {
        if (journeyPatternLayover == null) {
            journeyPatternLayover = new ArrayList<JourneyPatternLayover>();
        }
        return this.journeyPatternLayover;
    }

}
