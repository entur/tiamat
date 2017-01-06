package org.rutebanken.tiamat.model;

public class AccessSpace_VersionStructure
        extends StopPlaceSpace_VersionStructure {

    protected AccessSpaceTypeEnumeration accessSpaceType;
    protected PassageTypeEnumeration passageType;
    protected AccessSpaceRefStructure parentAccessSpaceRef;

    public AccessSpaceTypeEnumeration getAccessSpaceType() {
        return accessSpaceType;
    }

    public void setAccessSpaceType(AccessSpaceTypeEnumeration value) {
        this.accessSpaceType = value;
    }

    public PassageTypeEnumeration getPassageType() {
        return passageType;
    }

    public void setPassageType(PassageTypeEnumeration value) {
        this.passageType = value;
    }

    public AccessSpaceRefStructure getParentAccessSpaceRef() {
        return parentAccessSpaceRef;
    }

    public void setParentAccessSpaceRef(AccessSpaceRefStructure value) {
        this.parentAccessSpaceRef = value;
    }

}
