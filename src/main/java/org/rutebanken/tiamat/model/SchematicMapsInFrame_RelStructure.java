package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class SchematicMapsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<SchematicMap> schematicMap;

    public List<SchematicMap> getSchematicMap() {
        if (schematicMap == null) {
            schematicMap = new ArrayList<SchematicMap>();
        }
        return this.schematicMap;
    }

}
