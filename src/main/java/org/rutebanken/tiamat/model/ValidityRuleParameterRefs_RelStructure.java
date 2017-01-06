package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ValidityRuleParameterRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<ValidityRuleParameterRefStructure> validityRuleParameterRef;

    public List<ValidityRuleParameterRefStructure> getValidityRuleParameterRef() {
        if (validityRuleParameterRef == null) {
            validityRuleParameterRef = new ArrayList<ValidityRuleParameterRefStructure>();
        }
        return this.validityRuleParameterRef;
    }

}
