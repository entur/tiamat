package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TypeOfPlaceRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<TypeOfPlaceRefStructure> typeOfPlaceRef;

    public List<TypeOfPlaceRefStructure> getTypeOfPlaceRef() {
        if (typeOfPlaceRef == null) {
            typeOfPlaceRef = new ArrayList<TypeOfPlaceRefStructure>();
        }
        return this.typeOfPlaceRef;
    }

}
