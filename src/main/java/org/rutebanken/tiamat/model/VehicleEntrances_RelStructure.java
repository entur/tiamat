package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class VehicleEntrances_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> vehicleEntranceRefOrVehicleEntrance;

    public List<Object> getVehicleEntranceRefOrVehicleEntrance() {
        if (vehicleEntranceRefOrVehicleEntrance == null) {
            vehicleEntranceRefOrVehicleEntrance = new ArrayList<Object>();
        }
        return this.vehicleEntranceRefOrVehicleEntrance;
    }

}
