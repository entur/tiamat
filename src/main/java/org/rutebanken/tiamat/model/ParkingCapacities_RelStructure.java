package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ParkingCapacities_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> parkingCapacityRefOrParkingCapacity;

    public List<Object> getParkingCapacityRefOrParkingCapacity() {
        if (parkingCapacityRefOrParkingCapacity == null) {
            parkingCapacityRefOrParkingCapacity = new ArrayList<Object>();
        }
        return this.parkingCapacityRefOrParkingCapacity;
    }

}
