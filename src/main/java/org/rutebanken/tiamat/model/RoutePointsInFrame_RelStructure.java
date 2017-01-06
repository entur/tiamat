package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class RoutePointsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<RoutePoint> routePoint;

    public List<RoutePoint> getRoutePoint() {
        if (routePoint == null) {
            routePoint = new ArrayList<RoutePoint>();
        }
        return this.routePoint;
    }

}
