package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PointOfInterestRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<PointOfInterestRefStructure> pointOfInterestRef;

    public List<PointOfInterestRefStructure> getPointOfInterestRef() {
        if (pointOfInterestRef == null) {
            pointOfInterestRef = new ArrayList<PointOfInterestRefStructure>();
        }
        return this.pointOfInterestRef;
    }

}
