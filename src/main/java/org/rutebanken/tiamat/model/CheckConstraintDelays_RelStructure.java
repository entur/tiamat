package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CheckConstraintDelays_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<Object> checkConstraintDelayRefOrCheckConstraintDelay;

    public List<Object> getCheckConstraintDelayRefOrCheckConstraintDelay() {
        if (checkConstraintDelayRefOrCheckConstraintDelay == null) {
            checkConstraintDelayRefOrCheckConstraintDelay = new ArrayList<Object>();
        }
        return this.checkConstraintDelayRefOrCheckConstraintDelay;
    }

}
