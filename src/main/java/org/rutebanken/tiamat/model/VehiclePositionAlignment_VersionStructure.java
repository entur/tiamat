package org.rutebanken.tiamat.model;

public class VehiclePositionAlignment_VersionStructure
        extends VersionedChildStructure {

    protected VehicleStoppingPositionRefStructure vehicleStoppingPositionRef;
    protected BoardingPositionRefStructure boardingPositionRef;
    protected StopPlaceEntranceRefStructure boardingPositionEntranceRef;

    public VehicleStoppingPositionRefStructure getVehicleStoppingPositionRef() {
        return vehicleStoppingPositionRef;
    }

    public void setVehicleStoppingPositionRef(VehicleStoppingPositionRefStructure value) {
        this.vehicleStoppingPositionRef = value;
    }

    public BoardingPositionRefStructure getBoardingPositionRef() {
        return boardingPositionRef;
    }

    public void setBoardingPositionRef(BoardingPositionRefStructure value) {
        this.boardingPositionRef = value;
    }

    public StopPlaceEntranceRefStructure getBoardingPositionEntranceRef() {
        return boardingPositionEntranceRef;
    }

    public void setBoardingPositionEntranceRef(StopPlaceEntranceRefStructure value) {
        this.boardingPositionEntranceRef = value;
    }

}
