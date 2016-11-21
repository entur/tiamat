package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PointOfInterestClassificationHierarchiesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<PointOfInterestClassificationHierarchy> pointOfInterestClassificationHierarchy;

    public List<PointOfInterestClassificationHierarchy> getPointOfInterestClassificationHierarchy() {
        if (pointOfInterestClassificationHierarchy == null) {
            pointOfInterestClassificationHierarchy = new ArrayList<PointOfInterestClassificationHierarchy>();
        }
        return this.pointOfInterestClassificationHierarchy;
    }

}
