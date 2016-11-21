

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class VehiclePositionAlignment_VersionStructure
    extends VersionedChildStructure
{

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
