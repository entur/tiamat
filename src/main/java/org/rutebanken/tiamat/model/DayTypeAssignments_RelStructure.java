package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DayTypeAssignments_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<DayTypeAssignment> dayTypeAssignment;

    public List<DayTypeAssignment> getDayTypeAssignment() {
        if (dayTypeAssignment == null) {
            dayTypeAssignment = new ArrayList<DayTypeAssignment>();
        }
        return this.dayTypeAssignment;
    }

}
