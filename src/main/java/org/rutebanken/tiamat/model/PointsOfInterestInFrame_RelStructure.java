package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PointsOfInterestInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<PointOfInterest> pointOfInterest;

    public List<PointOfInterest> getPointOfInterest() {
        if (pointOfInterest == null) {
            pointOfInterest = new ArrayList<PointOfInterest>();
        }
        return this.pointOfInterest;
    }

}
