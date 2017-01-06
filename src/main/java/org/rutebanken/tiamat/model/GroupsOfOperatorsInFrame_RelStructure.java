package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GroupsOfOperatorsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<GroupOfOperators> groupOfOperators;

    public List<GroupOfOperators> getGroupOfOperators() {
        if (groupOfOperators == null) {
            groupOfOperators = new ArrayList<GroupOfOperators>();
        }
        return this.groupOfOperators;
    }

}
