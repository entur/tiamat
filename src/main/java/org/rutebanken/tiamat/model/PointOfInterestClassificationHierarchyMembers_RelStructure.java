

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PointOfInterestClassificationHierarchyMembers_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<PointOfInterestClassificationHierarchyMemberStructure> classificationHierarchyMember;

    public List<PointOfInterestClassificationHierarchyMemberStructure> getClassificationHierarchyMember() {
        if (classificationHierarchyMember == null) {
            classificationHierarchyMember = new ArrayList<PointOfInterestClassificationHierarchyMemberStructure>();
        }
    }

}
