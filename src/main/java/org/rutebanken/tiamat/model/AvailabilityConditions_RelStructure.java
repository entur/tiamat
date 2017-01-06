package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class AvailabilityConditions_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> availabilityConditionRefOrAvailabilityConditionOrValidDuring;

    public List<Object> getAvailabilityConditionRefOrAvailabilityConditionOrValidDuring() {
        if (availabilityConditionRefOrAvailabilityConditionOrValidDuring == null) {
            availabilityConditionRefOrAvailabilityConditionOrValidDuring = new ArrayList<Object>();
        }
        return this.availabilityConditionRefOrAvailabilityConditionOrValidDuring;
    }

}
