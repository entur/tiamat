package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ValidityTriggers_RelStructure
        extends ContainmentAggregationStructure {

    protected List<ValidityTrigger> validityTrigger;

    public List<ValidityTrigger> getValidityTrigger() {
        if (validityTrigger == null) {
            validityTrigger = new ArrayList<ValidityTrigger>();
        }
        return this.validityTrigger;
    }

}
