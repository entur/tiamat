package org.rutebanken.tiamat.model.vehicle;

import org.rutebanken.tiamat.model.ContainmentAggregationStructure;

import java.util.ArrayList;
import java.util.List;

public class VehicleManoeuvringRequirements_RelStructure extends ContainmentAggregationStructure {

    private List<Object> vehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement;

    public List<Object> getVehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement() {
        if (this.vehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement == null) {
            this.vehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement = new ArrayList();
        }

        return this.vehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement;
    }
}
