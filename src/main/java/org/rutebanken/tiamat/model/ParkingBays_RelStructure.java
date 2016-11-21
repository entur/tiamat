package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ParkingBays_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> parkingBayRefOrParkingBay;

    public List<Object> getParkingBayRefOrParkingBay() {
        if (parkingBayRefOrParkingBay == null) {
            parkingBayRefOrParkingBay = new ArrayList<Object>();
        }
        return this.parkingBayRefOrParkingBay;
    }

}
