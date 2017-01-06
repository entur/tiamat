package org.rutebanken.tiamat.model;

public class PassengerCarryingRequirement_VersionStructure
        extends VehicleRequirement_VersionStructure {

    protected PassengerCapacity passengerCapacity;
    protected Boolean lowFloor;
    protected Boolean hasLiftOrRamp;
    protected Boolean hasHoist;

    public PassengerCapacity getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(PassengerCapacity value) {
        this.passengerCapacity = value;
    }

    public Boolean isLowFloor() {
        return lowFloor;
    }

    public void setLowFloor(Boolean value) {
        this.lowFloor = value;
    }

    public Boolean isHasLiftOrRamp() {
        return hasLiftOrRamp;
    }

    public void setHasLiftOrRamp(Boolean value) {
        this.hasLiftOrRamp = value;
    }

    public Boolean isHasHoist() {
        return hasHoist;
    }

    public void setHasHoist(Boolean value) {
        this.hasHoist = value;
    }

}
