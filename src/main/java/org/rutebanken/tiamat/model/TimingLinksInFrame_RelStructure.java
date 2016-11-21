package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TimingLinksInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<TimingLink> timingLink;

    public List<TimingLink> getTimingLink() {
        if (timingLink == null) {
            timingLink = new ArrayList<TimingLink>();
        }
        return this.timingLink;
    }

}
