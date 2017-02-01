package org.rutebanken.tiamat.model;

public class PlaceInSequence_VersionedChildStructure
        extends PointInLinkSequence_VersionedChildStructure {

    protected PlaceRefStructure placeRef;
    protected String branchLevel;
    protected MultilingualStringEntity description;

    public PlaceRefStructure getPlaceRef() {
        return placeRef;
    }

    public void setPlaceRef(PlaceRefStructure value) {
        this.placeRef = value;
    }

    public String getBranchLevel() {
        return branchLevel;
    }

    public void setBranchLevel(String value) {
        this.branchLevel = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

}
