package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PointOfInterestClassificationsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<PointOfInterestClassification> pointOfInterestClassification;

    public List<PointOfInterestClassification> getPointOfInterestClassification() {
        if (pointOfInterestClassification == null) {
            pointOfInterestClassification = new ArrayList<PointOfInterestClassification>();
        }
        return this.pointOfInterestClassification;
    }

}
