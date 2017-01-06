package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GaragePoints_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<Object> garagePointRefOrGaragePoint;

    public List<Object> getGaragePointRefOrGaragePoint() {
        if (garagePointRefOrGaragePoint == null) {
            garagePointRefOrGaragePoint = new ArrayList<Object>();
        }
        return this.garagePointRefOrGaragePoint;
    }

}
