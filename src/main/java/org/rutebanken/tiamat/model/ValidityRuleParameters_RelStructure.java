package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ValidityRuleParameters_RelStructure
        extends ContainmentAggregationStructure {

    protected List<ValidityRuleParameter> validityRuleParameter;

    public List<ValidityRuleParameter> getValidityRuleParameter() {
        if (validityRuleParameter == null) {
            validityRuleParameter = new ArrayList<ValidityRuleParameter>();
        }
        return this.validityRuleParameter;
    }

}
