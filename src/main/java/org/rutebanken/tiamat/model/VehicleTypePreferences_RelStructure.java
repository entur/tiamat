package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class VehicleTypePreferences_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<VehicleTypePreference> vehicleTypePreference;

    public List<VehicleTypePreference> getVehicleTypePreference() {
        if (vehicleTypePreference == null) {
            vehicleTypePreference = new ArrayList<VehicleTypePreference>();
        }
        return this.vehicleTypePreference;
    }

}
