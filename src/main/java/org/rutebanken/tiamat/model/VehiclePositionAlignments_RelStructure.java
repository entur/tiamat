package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class VehiclePositionAlignments_RelStructure
        extends ContainmentAggregationStructure {

    protected List<VehiclePositionAlignment> vehiclePositionAlignment;

    public List<VehiclePositionAlignment> getVehiclePositionAlignment() {
        if (vehiclePositionAlignment == null) {
            vehiclePositionAlignment = new ArrayList<VehiclePositionAlignment>();
        }
        return this.vehiclePositionAlignment;
    }

}
