package org.rutebanken.tiamat.model;

public class GroupOfLines_VersionStructure
        extends GroupOfEntities_VersionStructure {

    protected LineRefs_RelStructure members;
    protected LineRefStructure mainLineRef;
    protected AllVehicleModesOfTransportEnumeration transportMode;

    public LineRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(LineRefs_RelStructure value) {
        this.members = value;
    }

    public LineRefStructure getMainLineRef() {
        return mainLineRef;
    }

    public void setMainLineRef(LineRefStructure value) {
        this.mainLineRef = value;
    }

    public AllVehicleModesOfTransportEnumeration getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(AllVehicleModesOfTransportEnumeration value) {
        this.transportMode = value;
    }

}
