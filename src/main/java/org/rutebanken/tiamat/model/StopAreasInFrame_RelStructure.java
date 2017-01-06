package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class StopAreasInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<StopArea> stopArea;

    public List<StopArea> getStopArea() {
        if (stopArea == null) {
            stopArea = new ArrayList<StopArea>();
        }
        return this.stopArea;
    }

}
