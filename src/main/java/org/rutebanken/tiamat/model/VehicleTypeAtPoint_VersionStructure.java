package org.rutebanken.tiamat.model;

import java.math.BigInteger;


public class VehicleTypeAtPoint_VersionStructure
        extends NetworkRestriction_VersionStructure {

    protected VehicleTypeRefStructure forVehicleTypeRef;
    protected BigInteger capacity;

    public VehicleTypeRefStructure getForVehicleTypeRef() {
        return forVehicleTypeRef;
    }

    public void setForVehicleTypeRef(VehicleTypeRefStructure value) {
        this.forVehicleTypeRef = value;
    }

    public BigInteger getCapacity() {
        return capacity;
    }

    public void setCapacity(BigInteger value) {
        this.capacity = value;
    }

}
