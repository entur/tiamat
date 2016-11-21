

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class SchematicMapMembers_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<SchematicMapMember_VersionedChildStructure> schematicMapMember;

    public List<SchematicMapMember_VersionedChildStructure> getSchematicMapMember() {
        if (schematicMapMember == null) {
            schematicMapMember = new ArrayList<SchematicMapMember_VersionedChildStructure>();
        }
        return this.schematicMapMember;
    }

}
