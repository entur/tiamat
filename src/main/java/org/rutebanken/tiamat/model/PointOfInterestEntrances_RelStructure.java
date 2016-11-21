package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PointOfInterestEntrances_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> pointOfInterestEntranceRefOrPointOfInterestEntrance;

    public List<Object> getPointOfInterestEntranceRefOrPointOfInterestEntrance() {
        if (pointOfInterestEntranceRefOrPointOfInterestEntrance == null) {
            pointOfInterestEntranceRefOrPointOfInterestEntrance = new ArrayList<Object>();
        }
        return this.pointOfInterestEntranceRefOrPointOfInterestEntrance;
    }

}
