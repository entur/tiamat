package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class LinkTypeRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<TypeOfLinkRefStructure> typeOfLinkRef;

    public List<TypeOfLinkRefStructure> getTypeOfLinkRef() {
        if (typeOfLinkRef == null) {
            typeOfLinkRef = new ArrayList<TypeOfLinkRefStructure>();
        }
        return this.typeOfLinkRef;
    }

}
