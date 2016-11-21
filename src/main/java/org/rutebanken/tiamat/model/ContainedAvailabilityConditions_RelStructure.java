package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ContainedAvailabilityConditions_RelStructure
        extends ContainmentAggregationStructure {

    protected List<AvailabilityCondition> availabilityCondition;

    public List<AvailabilityCondition> getAvailabilityCondition() {
        if (availabilityCondition == null) {
            availabilityCondition = new ArrayList<AvailabilityCondition>();
        }
        return this.availabilityCondition;
    }

}
