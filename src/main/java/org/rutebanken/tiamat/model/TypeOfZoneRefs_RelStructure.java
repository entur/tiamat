package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TypeOfZoneRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<TypeOfZoneRefStructure> typeOfZoneRef;

    public List<TypeOfZoneRefStructure> getTypeOfZoneRef() {
        if (typeOfZoneRef == null) {
            typeOfZoneRef = new ArrayList<TypeOfZoneRefStructure>();
        }
        return this.typeOfZoneRef;
    }

}
