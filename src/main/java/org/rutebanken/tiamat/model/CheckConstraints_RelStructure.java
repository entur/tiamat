package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CheckConstraints_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> checkConstraintRefOrCheckConstraint;

    public List<Object> getCheckConstraintRefOrCheckConstraint() {
        if (checkConstraintRefOrCheckConstraint == null) {
            checkConstraintRefOrCheckConstraint = new ArrayList<Object>();
        }
        return this.checkConstraintRefOrCheckConstraint;
    }

}
