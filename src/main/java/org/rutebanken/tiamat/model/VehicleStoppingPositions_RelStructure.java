package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class VehicleStoppingPositions_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> vehicleStoppingPositionRefOrVehicleStoppingPosition;

    public List<Object> getVehicleStoppingPositionRefOrVehicleStoppingPosition() {
        if (vehicleStoppingPositionRefOrVehicleStoppingPosition == null) {
            vehicleStoppingPositionRefOrVehicleStoppingPosition = new ArrayList<Object>();
        }
        return this.vehicleStoppingPositionRefOrVehicleStoppingPosition;
    }

}
