package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TimingPatternsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<TimingPattern> timingPattern;

    public List<TimingPattern> getTimingPattern() {
        if (timingPattern == null) {
            timingPattern = new ArrayList<TimingPattern>();
        }
        return this.timingPattern;
    }

}
