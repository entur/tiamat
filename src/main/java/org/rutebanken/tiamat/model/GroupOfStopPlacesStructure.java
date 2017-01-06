package org.rutebanken.tiamat.model;

public class GroupOfStopPlacesStructure
        extends GroupOfEntities_VersionStructure {

    protected String publicCode;
    protected StopPlaceRefs_RelStructure members;
    protected AlternativeNames_RelStructure alternativeNames;

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public StopPlaceRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(StopPlaceRefs_RelStructure value) {
        this.members = value;
    }

    public AlternativeNames_RelStructure getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(AlternativeNames_RelStructure value) {
        this.alternativeNames = value;
    }

}
