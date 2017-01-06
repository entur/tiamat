package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class OperationalContexRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<OperationalContextRefStructure> operationalContextRef;

    public List<OperationalContextRefStructure> getOperationalContextRef() {
        if (operationalContextRef == null) {
            operationalContextRef = new ArrayList<OperationalContextRefStructure>();
        }
        return this.operationalContextRef;
    }

}
