package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class FlexibleStopPlaceRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<FlexibleStopPlaceRefStructure> flexibleStopPlaceRef;

    public List<FlexibleStopPlaceRefStructure> getFlexibleStopPlaceRef() {
        if (flexibleStopPlaceRef == null) {
            flexibleStopPlaceRef = new ArrayList<FlexibleStopPlaceRefStructure>();
        }
        return this.flexibleStopPlaceRef;
    }

}
