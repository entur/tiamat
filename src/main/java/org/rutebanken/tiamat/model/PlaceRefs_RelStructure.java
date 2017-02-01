package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PlaceRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<PlaceRef> placeRef;

    public List<PlaceRef> getPlaceRef() {
        if (placeRef == null) {
            placeRef = new ArrayList<PlaceRef>();
        }
        return this.placeRef;
    }

}
