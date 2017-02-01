package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DestinationDisplaysInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<DestinationDisplay> destinationDisplay;

    public List<DestinationDisplay> getDestinationDisplay() {
        if (destinationDisplay == null) {
            destinationDisplay = new ArrayList<DestinationDisplay>();
        }
        return this.destinationDisplay;
    }

}
