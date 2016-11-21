package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GaragesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Garage> garage;

    public List<Garage> getGarage() {
        if (garage == null) {
            garage = new ArrayList<Garage>();
        }
        return this.garage;
    }

}
