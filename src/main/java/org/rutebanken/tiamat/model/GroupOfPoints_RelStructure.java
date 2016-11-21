package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GroupOfPoints_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<GroupOfPoints> groupOfPoints;

    public List<GroupOfPoints> getGroupOfPoints() {
        if (groupOfPoints == null) {
            groupOfPoints = new ArrayList<GroupOfPoints>();
        }
        return this.groupOfPoints;
    }

}
