package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ActivationTypeRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<TypeOfActivationRefStructure> typeOfActivationRef;

    public List<TypeOfActivationRefStructure> getTypeOfActivationRef() {
        if (typeOfActivationRef == null) {
            typeOfActivationRef = new ArrayList<TypeOfActivationRefStructure>();
        }
        return this.typeOfActivationRef;
    }

}
