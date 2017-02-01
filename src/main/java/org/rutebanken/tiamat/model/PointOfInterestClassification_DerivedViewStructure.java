package org.rutebanken.tiamat.model;

public class PointOfInterestClassification_DerivedViewStructure
        extends DerivedViewStructure {

    protected PointOfInterestClassificationRefStructure pointOfInterestClassificationRef;
    protected MultilingualStringEntity name;

    public PointOfInterestClassificationRefStructure getPointOfInterestClassificationRef() {
        return pointOfInterestClassificationRef;
    }

    public void setPointOfInterestClassificationRef(PointOfInterestClassificationRefStructure value) {
        this.pointOfInterestClassificationRef = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

}
