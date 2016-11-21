package org.rutebanken.tiamat.model;

public class PointOfInterestClassificationHierarchyMemberStructure
        extends VersionedChildStructure {

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
