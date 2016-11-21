package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TimingLinks_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<TimingLink> timingLink;

    public List<TimingLink> getTimingLink() {
        if (timingLink == null) {
            timingLink = new ArrayList<TimingLink>();
        }
        return this.timingLink;
    }

}
