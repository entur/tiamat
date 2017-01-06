package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GroupOfLinksInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<GroupOfLinks> groupOfLinks;

    public List<GroupOfLinks> getGroupOfLinks() {
        if (groupOfLinks == null) {
            groupOfLinks = new ArrayList<GroupOfLinks>();
        }
        return this.groupOfLinks;
    }

}
