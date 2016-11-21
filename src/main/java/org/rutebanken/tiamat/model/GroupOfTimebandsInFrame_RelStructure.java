package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GroupOfTimebandsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<GroupOfTimebands> groupOfTimebands;

    public List<GroupOfTimebands> getGroupOfTimebands() {
        if (groupOfTimebands == null) {
            groupOfTimebands = new ArrayList<GroupOfTimebands>();
        }
        return this.groupOfTimebands;
    }

}
