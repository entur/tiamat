package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ActivationAssignments_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> activationAssignmentRefOrActivationAssignment;

    public List<Object> getActivationAssignmentRefOrActivationAssignment() {
        if (activationAssignmentRefOrActivationAssignment == null) {
            activationAssignmentRefOrActivationAssignment = new ArrayList<Object>();
        }
        return this.activationAssignmentRefOrActivationAssignment;
    }

}
