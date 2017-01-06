package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GroupOfTimebands_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> groupOfTimebandsRefOrGroupOfTimebands;

    public List<Object> getGroupOfTimebandsRefOrGroupOfTimebands() {
        if (groupOfTimebandsRefOrGroupOfTimebands == null) {
            groupOfTimebandsRefOrGroupOfTimebands = new ArrayList<Object>();
        }
        return this.groupOfTimebandsRefOrGroupOfTimebands;
    }

}
