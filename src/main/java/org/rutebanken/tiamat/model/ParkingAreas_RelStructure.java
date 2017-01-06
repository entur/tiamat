package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ParkingAreas_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> parkingAreaRefOrParkingArea;

    public List<Object> getParkingAreaRefOrParkingArea() {
        if (parkingAreaRefOrParkingArea == null) {
            parkingAreaRefOrParkingArea = new ArrayList<Object>();
        }
        return this.parkingAreaRefOrParkingArea;
    }

}
