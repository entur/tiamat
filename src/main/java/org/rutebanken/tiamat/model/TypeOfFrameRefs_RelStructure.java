package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TypeOfFrameRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<TypeOfFrameRefStructure> typeOfFrameRef;

    public List<TypeOfFrameRefStructure> getTypeOfFrameRef() {
        if (typeOfFrameRef == null) {
            typeOfFrameRef = new ArrayList<TypeOfFrameRefStructure>();
        }
        return this.typeOfFrameRef;
    }

}
