package org.rutebanken.tiamat.model;

public class VehicleQuayAlignment_VersionStructure
        extends VersionedChildStructure {

    protected VehicleStoppingPlaceRefStructure vehicleStoppingPlaceRef;
    protected QuayReference quayRef;

    public VehicleStoppingPlaceRefStructure getVehicleStoppingPlaceRef() {
        return vehicleStoppingPlaceRef;
    }

    public void setVehicleStoppingPlaceRef(VehicleStoppingPlaceRefStructure value) {
        this.vehicleStoppingPlaceRef = value;
    }

    public QuayReference getQuayRef() {
        return quayRef;
    }

    public void setQuayRef(QuayReference value) {
        this.quayRef = value;
    }

}
