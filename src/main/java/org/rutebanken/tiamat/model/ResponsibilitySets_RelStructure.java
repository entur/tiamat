package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ResponsibilitySets_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> responsibilitySetRefOrResponsibilitySet;

    public List<Object> getResponsibilitySetRefOrResponsibilitySet() {
        if (responsibilitySetRefOrResponsibilitySet == null) {
            responsibilitySetRefOrResponsibilitySet = new ArrayList<Object>();
        }
        return this.responsibilitySetRefOrResponsibilitySet;
    }

}
