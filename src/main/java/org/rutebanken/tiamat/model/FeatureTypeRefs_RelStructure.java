package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class FeatureTypeRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<TypeOfFeatureRefStructure> typeOfFeatureRef;

    public List<TypeOfFeatureRefStructure> getTypeOfFeatureRef() {
        if (typeOfFeatureRef == null) {
            typeOfFeatureRef = new ArrayList<TypeOfFeatureRefStructure>();
        }
        return this.typeOfFeatureRef;
    }

}
