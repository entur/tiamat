package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TimebandsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Timeband> timeband;

    public List<Timeband> getTimeband() {
        if (timeband == null) {
            timeband = new ArrayList<Timeband>();
        }
        return this.timeband;
    }

}
