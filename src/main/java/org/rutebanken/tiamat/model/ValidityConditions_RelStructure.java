package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ValidityConditions_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> validityConditionRefOrValidBetweenOrValidityCondition_;

    public List<Object> getValidityConditionRefOrValidBetweenOrValidityCondition_() {
        if (validityConditionRefOrValidBetweenOrValidityCondition_ == null) {
            validityConditionRefOrValidBetweenOrValidityCondition_ = new ArrayList<Object>();
        }
        return this.validityConditionRefOrValidBetweenOrValidityCondition_;
    }

}
