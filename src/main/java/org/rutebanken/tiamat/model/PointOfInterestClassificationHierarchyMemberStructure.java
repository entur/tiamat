

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


    "pointOfInterestHierarchyRef",
    "parentClassificationRef",
public class PointOfInterestClassificationHierarchyMemberStructure
    extends VersionedChildStructure
{

    protected PointOfInterestHierarchyRefStructure pointOfInterestHierarchyRef;
    protected PointOfInterestClassificationRefStructure parentClassificationRef;
    protected PointOfInterestClassificationRefStructure pointOfInterestClassificationRef;

    public PointOfInterestHierarchyRefStructure getPointOfInterestHierarchyRef() {
        return pointOfInterestHierarchyRef;
    }

    public void setPointOfInterestHierarchyRef(PointOfInterestHierarchyRefStructure value) {
        this.pointOfInterestHierarchyRef = value;
    }

    public PointOfInterestClassificationRefStructure getParentClassificationRef() {
        return parentClassificationRef;
    }

    public void setParentClassificationRef(PointOfInterestClassificationRefStructure value) {
        this.parentClassificationRef = value;
    }

    public PointOfInterestClassificationRefStructure getPointOfInterestClassificationRef() {
        return pointOfInterestClassificationRef;
    }

    public void setPointOfInterestClassificationRef(PointOfInterestClassificationRefStructure value) {
        this.pointOfInterestClassificationRef = value;
    }

}
