package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class JourneyLayovers_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<JourneyLayover> journeyLayover;

    public List<JourneyLayover> getJourneyLayover() {
        if (journeyLayover == null) {
            journeyLayover = new ArrayList<JourneyLayover>();
        }
        return this.journeyLayover;
    }

}
