package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ResponsibilityRoleAssignments_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<ResponsibilityRoleAssignment_VersionedChildStructure> responsibilityRoleAssignment;

    public List<ResponsibilityRoleAssignment_VersionedChildStructure> getResponsibilityRoleAssignment() {
        if (responsibilityRoleAssignment == null) {
            responsibilityRoleAssignment = new ArrayList<ResponsibilityRoleAssignment_VersionedChildStructure>();
        }
        return this.responsibilityRoleAssignment;
    }

}
