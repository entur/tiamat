package org.rutebanken.tiamat.model;

public class MeetingRestriction_VersionStructure
        extends InfrastructureLinkRestriction_VersionStructure {

    protected VehicleTypeRefStructure forVehicleTypeRef;
    protected VehicleTypeRefStructure againstVehicleTypeRef;

    public VehicleTypeRefStructure getForVehicleTypeRef() {
        return forVehicleTypeRef;
    }

    public void setForVehicleTypeRef(VehicleTypeRefStructure value) {
        this.forVehicleTypeRef = value;
    }

    public VehicleTypeRefStructure getAgainstVehicleTypeRef() {
        return againstVehicleTypeRef;
    }

    public void setAgainstVehicleTypeRef(VehicleTypeRefStructure value) {
        this.againstVehicleTypeRef = value;
    }

}
