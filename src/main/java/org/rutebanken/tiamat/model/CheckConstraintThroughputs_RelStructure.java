package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CheckConstraintThroughputs_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<Object> checkConstraintThroughputRefOrCheckConstraintThroughput;

    public List<Object> getCheckConstraintThroughputRefOrCheckConstraintThroughput() {
        if (checkConstraintThroughputRefOrCheckConstraintThroughput == null) {
            checkConstraintThroughputRefOrCheckConstraintThroughput = new ArrayList<Object>();
        }
        return this.checkConstraintThroughputRefOrCheckConstraintThroughput;
    }

}
