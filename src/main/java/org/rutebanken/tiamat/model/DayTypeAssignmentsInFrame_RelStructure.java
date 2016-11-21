package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DayTypeAssignmentsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<DayTypeAssignment> dayTypeAssignment;

    public List<DayTypeAssignment> getDayTypeAssignment() {
        if (dayTypeAssignment == null) {
            dayTypeAssignment = new ArrayList<DayTypeAssignment>();
        }
        return this.dayTypeAssignment;
    }

}
