package org.rutebanken.tiamat.model;

public class PlaceInSequence_VersionedChildStructure
        extends PointInLinkSequence_VersionedChildStructure {

    protected PlaceRefStructure placeRef;
    protected String branchLevel;
    protected MultilingualStringEntity description;
    protected OnwardLinks onwardLinks;

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

    public OnwardLinks getOnwardLinks() {
        return onwardLinks;
    }

    public void setOnwardLinks(OnwardLinks value) {
        this.onwardLinks = value;
    }

}
