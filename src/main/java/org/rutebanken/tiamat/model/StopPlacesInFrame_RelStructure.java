package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class StopPlacesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<StopPlace> stopPlace;

    public List<StopPlace> getStopPlace() {
        if (stopPlace == null) {
            stopPlace = new ArrayList<StopPlace>();
        }
        return this.stopPlace;
    }

}
