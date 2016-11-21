package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TimingPointsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<TimingPoint> timingPoint;

    public List<TimingPoint> getTimingPoint() {
        if (timingPoint == null) {
            timingPoint = new ArrayList<TimingPoint>();
        }
        return this.timingPoint;
    }

}
