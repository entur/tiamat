package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GroupsOfLinesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<GroupOfLines> groupOfLines;

    public List<GroupOfLines> getGroupOfLines() {
        if (groupOfLines == null) {
            groupOfLines = new ArrayList<GroupOfLines>();
        }
        return this.groupOfLines;
    }

}
