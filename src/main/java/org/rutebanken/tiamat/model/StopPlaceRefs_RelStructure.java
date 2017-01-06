package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class StopPlaceRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<StopPlaceReference> stopPlaceRef;

    public List<StopPlaceReference> getStopPlaceRef() {
        if (stopPlaceRef == null) {
            stopPlaceRef = new ArrayList<StopPlaceReference>();
        }
        return this.stopPlaceRef;
    }

}
