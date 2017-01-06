package org.rutebanken.tiamat.model;

public class PointOfInterest_DerivedViewStructure
        extends DerivedViewStructure {

    protected PointOfInterestRefStructure pointOfInterestRef;
    protected MultilingualStringEntity name;
    protected TypeOfPlaceRefs_RelStructure placeTypes;
    protected MultilingualStringEntity shortName;

    public PointOfInterestRefStructure getPointOfInterestRef() {
        return pointOfInterestRef;
    }

    public void setPointOfInterestRef(PointOfInterestRefStructure value) {
        this.pointOfInterestRef = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public TypeOfPlaceRefs_RelStructure getPlaceTypes() {
        return placeTypes;
    }

    public void setPlaceTypes(TypeOfPlaceRefs_RelStructure value) {
        this.placeTypes = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

}
