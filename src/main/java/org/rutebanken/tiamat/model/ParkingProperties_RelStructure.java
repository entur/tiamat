package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ParkingProperties_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<ParkingProperties> parkingProperties;

    public List<ParkingProperties> getParkingProperties() {
        if (parkingProperties == null) {
            parkingProperties = new ArrayList<ParkingProperties>();
        }
        return this.parkingProperties;
    }

}
