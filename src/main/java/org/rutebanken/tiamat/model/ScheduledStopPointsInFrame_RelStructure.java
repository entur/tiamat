package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ScheduledStopPointsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<ScheduledStopPoint> scheduledStopPoint;

    public List<ScheduledStopPoint> getScheduledStopPoint() {
        if (scheduledStopPoint == null) {
            scheduledStopPoint = new ArrayList<ScheduledStopPoint>();
        }
        return this.scheduledStopPoint;
    }

}
