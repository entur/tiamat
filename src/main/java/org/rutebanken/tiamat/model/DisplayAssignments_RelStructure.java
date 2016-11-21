package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DisplayAssignments_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> displayAssignmentRefOrDisplayAssignment;

    public List<Object> getDisplayAssignmentRefOrDisplayAssignment() {
        if (displayAssignmentRefOrDisplayAssignment == null) {
            displayAssignmentRefOrDisplayAssignment = new ArrayList<Object>();
        }
        return this.displayAssignmentRefOrDisplayAssignment;
    }

}
