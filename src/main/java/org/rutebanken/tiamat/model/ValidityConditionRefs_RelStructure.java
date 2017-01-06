package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class ValidityConditionRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends ValidityConditionRefStructure>> validityConditionRef;

    public List<JAXBElement<? extends ValidityConditionRefStructure>> getValidityConditionRef() {
        if (validityConditionRef == null) {
            validityConditionRef = new ArrayList<JAXBElement<? extends ValidityConditionRefStructure>>();
        }
        return this.validityConditionRef;
    }

}
