package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DestinationDisplayViews_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> destinationDisplayRefOrDestinationDisplayView;

    public List<Object> getDestinationDisplayRefOrDestinationDisplayView() {
        if (destinationDisplayRefOrDestinationDisplayView == null) {
            destinationDisplayRefOrDestinationDisplayView = new ArrayList<Object>();
        }
        return this.destinationDisplayRefOrDestinationDisplayView;
    }

}
