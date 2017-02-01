package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ValidityTriggerRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<ValidityTriggerRefStructure> validityTriggerRef;

    public List<ValidityTriggerRefStructure> getValidityTriggerRef() {
        if (validityTriggerRef == null) {
            validityTriggerRef = new ArrayList<ValidityTriggerRefStructure>();
        }
        return this.validityTriggerRef;
    }

}
