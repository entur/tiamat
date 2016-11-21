package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PointsOnRoute_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<PointOnRoute> pointOnRoute;

    public List<PointOnRoute> getPointOnRoute() {
        if (pointOnRoute == null) {
            pointOnRoute = new ArrayList<PointOnRoute>();
        }
        return this.pointOnRoute;
    }

}
