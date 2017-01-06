package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class VehicleManoeuvringRequirements_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> vehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement;

    public List<Object> getVehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement() {
        if (vehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement == null) {
            vehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement = new ArrayList<Object>();
        }
        return this.vehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement;
    }

}
