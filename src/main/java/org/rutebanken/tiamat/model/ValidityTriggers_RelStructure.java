package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ValidityTriggers_RelStructure
        extends ContainmentAggregationStructure {

    protected List<ValidityTrigger_VersionStructure> validityTrigger;

    public List<ValidityTrigger_VersionStructure> getValidityTrigger() {
        if (validityTrigger == null) {
            validityTrigger = new ArrayList<ValidityTrigger_VersionStructure>();
        }
        return this.validityTrigger;
    }

}
