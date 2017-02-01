package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ValidityRuleParameters_RelStructure
        extends ContainmentAggregationStructure {

    protected List<ValidityRuleParameter_VersionStructure> validityRuleParameter;

    public List<ValidityRuleParameter_VersionStructure> getValidityRuleParameter() {
        if (validityRuleParameter == null) {
            validityRuleParameter = new ArrayList<ValidityRuleParameter_VersionStructure>();
        }
        return this.validityRuleParameter;
    }

}
