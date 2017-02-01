package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class AccessesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Access> access;

    public List<Access> getAccess() {
        if (access == null) {
            access = new ArrayList<Access>();
        }
        return this.access;
    }

}
