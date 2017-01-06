package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class VehicleModelsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<VehicleModel> vehicleModel;

    public List<VehicleModel> getVehicleModel() {
        if (vehicleModel == null) {
            vehicleModel = new ArrayList<VehicleModel>();
        }
        return this.vehicleModel;
    }

}
