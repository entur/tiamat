package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GroupOfLinkSequences_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<GroupOfLinkSequences> groupOfLinkSequences;

    public List<GroupOfLinkSequences> getGroupOfLinkSequences() {
        if (groupOfLinkSequences == null) {
            groupOfLinkSequences = new ArrayList<GroupOfLinkSequences>();
        }
        return this.groupOfLinkSequences;
    }

}
