package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CodespaceAssignments_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<CodespaceAssignment_VersionedChildStructure> codespaceAssignment;

    public List<CodespaceAssignment_VersionedChildStructure> getCodespaceAssignment() {
        if (codespaceAssignment == null) {
            codespaceAssignment = new ArrayList<CodespaceAssignment_VersionedChildStructure>();
        }
        return this.codespaceAssignment;
    }

}
