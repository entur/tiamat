package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DirectionsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Direction> direction;

    public List<Direction> getDirection() {
        if (direction == null) {
            direction = new ArrayList<Direction>();
        }
        return this.direction;
    }

}
