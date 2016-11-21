package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class SchematicMapMembers_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<SchematicMapMember_VersionedChildStructure> schematicMapMember;

    public List<SchematicMapMember_VersionedChildStructure> getSchematicMapMember() {
        if (schematicMapMember == null) {
            schematicMapMember = new ArrayList<SchematicMapMember_VersionedChildStructure>();
        }
        return this.schematicMapMember;
    }

}
