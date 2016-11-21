package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TimeDemandTypeAssignmentsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<TimeDemandTypeAssignment> timeDemandTypeAssignment;

    public List<TimeDemandTypeAssignment> getTimeDemandTypeAssignment() {
        if (timeDemandTypeAssignment == null) {
            timeDemandTypeAssignment = new ArrayList<TimeDemandTypeAssignment>();
        }
        return this.timeDemandTypeAssignment;
    }

}
