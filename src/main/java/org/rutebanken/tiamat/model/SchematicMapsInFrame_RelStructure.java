

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class SchematicMapsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<SchematicMap> schematicMap;

    public List<SchematicMap> getSchematicMap() {
        if (schematicMap == null) {
            schematicMap = new ArrayList<SchematicMap>();
        }
        return this.schematicMap;
    }

}
