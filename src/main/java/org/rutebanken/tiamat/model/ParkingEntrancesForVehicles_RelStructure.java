package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ParkingEntrancesForVehicles_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> parkingEntranceForVehiclesRefOrParkingEntranceForVehicles;

    public List<Object> getParkingEntranceForVehiclesRefOrParkingEntranceForVehicles() {
        if (parkingEntranceForVehiclesRefOrParkingEntranceForVehicles == null) {
            parkingEntranceForVehiclesRefOrParkingEntranceForVehicles = new ArrayList<Object>();
        }
        return this.parkingEntranceForVehiclesRefOrParkingEntranceForVehicles;
    }

}
