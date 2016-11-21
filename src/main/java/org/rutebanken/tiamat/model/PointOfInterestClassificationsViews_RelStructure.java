package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PointOfInterestClassificationsViews_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<Object> pointOfInterestClassificationRefOrPointOfInterestClassificationView;

    public List<Object> getPointOfInterestClassificationRefOrPointOfInterestClassificationView() {
        if (pointOfInterestClassificationRefOrPointOfInterestClassificationView == null) {
            pointOfInterestClassificationRefOrPointOfInterestClassificationView = new ArrayList<Object>();
        }
        return this.pointOfInterestClassificationRefOrPointOfInterestClassificationView;
    }

}
