package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DisplayAssignmentsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<DisplayAssignment> displayAssignment;

    public List<DisplayAssignment> getDisplayAssignment() {
        if (displayAssignment == null) {
            displayAssignment = new ArrayList<DisplayAssignment>();
        }
        return this.displayAssignment;
    }

}
