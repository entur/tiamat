package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class HailAndRideAreas_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> hailAndRideAreaRefOrHailAndRideArea;

    public List<Object> getHailAndRideAreaRefOrHailAndRideArea() {
        if (hailAndRideAreaRefOrHailAndRideArea == null) {
            hailAndRideAreaRefOrHailAndRideArea = new ArrayList<Object>();
        }
        return this.hailAndRideAreaRefOrHailAndRideArea;
    }

}
