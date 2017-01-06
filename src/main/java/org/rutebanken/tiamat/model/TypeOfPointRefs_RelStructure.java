package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TypeOfPointRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<TypeOfPointRefStructure> typeOfPointRef;

    public List<TypeOfPointRefStructure> getTypeOfPointRef() {
        if (typeOfPointRef == null) {
            typeOfPointRef = new ArrayList<TypeOfPointRefStructure>();
        }
        return this.typeOfPointRef;
    }

}
