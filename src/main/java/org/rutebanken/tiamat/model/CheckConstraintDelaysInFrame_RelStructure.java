package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CheckConstraintDelaysInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<CheckConstraintDelay> checkConstraintDelay;

    public List<CheckConstraintDelay> getCheckConstraintDelay() {
        if (checkConstraintDelay == null) {
            checkConstraintDelay = new ArrayList<CheckConstraintDelay>();
        }
        return this.checkConstraintDelay;
    }

}
