package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GroupOfLinks_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<GroupOfLinks> groupOfLinks;

    public List<GroupOfLinks> getGroupOfLinks() {
        if (groupOfLinks == null) {
            groupOfLinks = new ArrayList<GroupOfLinks>();
        }
        return this.groupOfLinks;
    }

}
