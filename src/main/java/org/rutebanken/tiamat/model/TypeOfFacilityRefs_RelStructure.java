package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TypeOfFacilityRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<TypeOfFacilityRefStructure> typeOfFacilityRef;

    public List<TypeOfFacilityRefStructure> getTypeOfFacilityRef() {
        if (typeOfFacilityRef == null) {
            typeOfFacilityRef = new ArrayList<TypeOfFacilityRefStructure>();
        }
        return this.typeOfFacilityRef;
    }

}
