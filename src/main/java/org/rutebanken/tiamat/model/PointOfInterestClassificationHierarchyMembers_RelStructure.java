package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PointOfInterestClassificationHierarchyMembers_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<PointOfInterestClassificationHierarchyMemberStructure> classificationHierarchyMember;

    public List<PointOfInterestClassificationHierarchyMemberStructure> getClassificationHierarchyMember() {
        if (classificationHierarchyMember == null) {
            classificationHierarchyMember = new ArrayList<PointOfInterestClassificationHierarchyMemberStructure>();
        }
        return this.classificationHierarchyMember;
    }

}
