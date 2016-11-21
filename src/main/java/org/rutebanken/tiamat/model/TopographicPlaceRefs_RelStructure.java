package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TopographicPlaceRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<TopographicPlaceRefStructure> topographicPlaceRef;

    public List<TopographicPlaceRefStructure> getTopographicPlaceRef() {
        if (topographicPlaceRef == null) {
            topographicPlaceRef = new ArrayList<TopographicPlaceRefStructure>();
        }
        return this.topographicPlaceRef;
    }

}
