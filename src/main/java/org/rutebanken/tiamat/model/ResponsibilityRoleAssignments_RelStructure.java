

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ResponsibilityRoleAssignments_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<ResponsibilityRoleAssignment_VersionedChildStructure> responsibilityRoleAssignment;

    public List<ResponsibilityRoleAssignment_VersionedChildStructure> getResponsibilityRoleAssignment() {
        if (responsibilityRoleAssignment == null) {
            responsibilityRoleAssignment = new ArrayList<ResponsibilityRoleAssignment_VersionedChildStructure>();
        }
        return this.responsibilityRoleAssignment;
    }

}
