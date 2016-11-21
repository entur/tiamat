package org.rutebanken.tiamat.model;

public class GroupOfPlaces_VersionStructure
        extends GroupOfEntities_VersionStructure {

    protected PlaceRefs_RelStructure members;
    protected CountryRef countryRef;
    protected PlaceRefStructure mainPlaceRef;

    public PlaceRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(PlaceRefs_RelStructure value) {
        this.members = value;
    }

    public CountryRef getCountryRef() {
        return countryRef;
    }

    public void setCountryRef(CountryRef value) {
        this.countryRef = value;
    }

    public PlaceRefStructure getMainPlaceRef() {
        return mainPlaceRef;
    }

    public void setMainPlaceRef(PlaceRefStructure value) {
        this.mainPlaceRef = value;
    }

}
