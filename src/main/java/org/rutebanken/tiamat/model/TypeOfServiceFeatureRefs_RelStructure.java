package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TypeOfServiceFeatureRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<TypeOfServiceFeatureRefStructure> typeOfServiceFeatureRef;

    public List<TypeOfServiceFeatureRefStructure> getTypeOfServiceFeatureRef() {
        if (typeOfServiceFeatureRef == null) {
            typeOfServiceFeatureRef = new ArrayList<TypeOfServiceFeatureRefStructure>();
        }
        return this.typeOfServiceFeatureRef;
    }

}
