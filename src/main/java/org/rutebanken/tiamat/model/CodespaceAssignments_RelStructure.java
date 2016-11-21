

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class CodespaceAssignments_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<CodespaceAssignment_VersionedChildStructure> codespaceAssignment;

    public List<CodespaceAssignment_VersionedChildStructure> getCodespaceAssignment() {
        if (codespaceAssignment == null) {
            codespaceAssignment = new ArrayList<CodespaceAssignment_VersionedChildStructure>();
        }
        return this.codespaceAssignment;
    }

}
