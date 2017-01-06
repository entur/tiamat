package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GroupOfTimingLinksInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<GroupOfTimingLinks> groupOfTimingLinks;

    public List<GroupOfTimingLinks> getGroupOfTimingLinks() {
        if (groupOfTimingLinks == null) {
            groupOfTimingLinks = new ArrayList<GroupOfTimingLinks>();
        }
        return this.groupOfTimingLinks;
    }

}
