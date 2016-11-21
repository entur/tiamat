package org.rutebanken.tiamat.model;

public abstract class RestrictedManoeuvre_VersionStructure
        extends InfrastructureLinkRestriction_VersionStructure {

    protected VehicleTypeRefStructure vehicleTypeRef;

    public VehicleTypeRefStructure getVehicleTypeRef() {
        return vehicleTypeRef;
    }

    public void setVehicleTypeRef(VehicleTypeRefStructure value) {
        this.vehicleTypeRef = value;
    }

}
