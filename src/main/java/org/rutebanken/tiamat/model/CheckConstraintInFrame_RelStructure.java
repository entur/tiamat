package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CheckConstraintInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<CheckConstraint> checkConstraint;

    public List<CheckConstraint> getCheckConstraint() {
        if (checkConstraint == null) {
            checkConstraint = new ArrayList<CheckConstraint>();
        }
        return this.checkConstraint;
    }

}
