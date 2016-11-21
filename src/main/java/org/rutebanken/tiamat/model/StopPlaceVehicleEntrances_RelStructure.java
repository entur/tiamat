package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class StopPlaceVehicleEntrances_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> vehicleEntranceRefOrStopPlaceVehicleEntrance;

    public List<Object> getVehicleEntranceRefOrStopPlaceVehicleEntrance() {
        if (vehicleEntranceRefOrStopPlaceVehicleEntrance == null) {
            vehicleEntranceRefOrStopPlaceVehicleEntrance = new ArrayList<Object>();
        }
        return this.vehicleEntranceRefOrStopPlaceVehicleEntrance;
    }

}
