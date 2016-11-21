package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TrafficControlPointsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<TrafficControlPoint> trafficControlPoint;

    public List<TrafficControlPoint> getTrafficControlPoint() {
        if (trafficControlPoint == null) {
            trafficControlPoint = new ArrayList<TrafficControlPoint>();
        }
        return this.trafficControlPoint;
    }

}
