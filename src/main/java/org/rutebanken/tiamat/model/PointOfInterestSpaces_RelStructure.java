package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PointOfInterestSpaces_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> pointOfInterestSpaceRefOrPointOfInterestSpace;

    public List<Object> getPointOfInterestSpaceRefOrPointOfInterestSpace() {
        if (pointOfInterestSpaceRefOrPointOfInterestSpace == null) {
            pointOfInterestSpaceRefOrPointOfInterestSpace = new ArrayList<Object>();
        }
        return this.pointOfInterestSpaceRefOrPointOfInterestSpace;
    }

}
