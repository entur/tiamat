package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class OperationalContextsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<OperationalContext> operationalContext;

    public List<OperationalContext> getOperationalContext() {
        if (operationalContext == null) {
            operationalContext = new ArrayList<OperationalContext>();
        }
        return this.operationalContext;
    }

}
