package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PlacesInSequence_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<PlaceInSequence> placeInSequence;

    public List<PlaceInSequence> getPlaceInSequence() {
        if (placeInSequence == null) {
            placeInSequence = new ArrayList<PlaceInSequence>();
        }
        return this.placeInSequence;
    }

}
